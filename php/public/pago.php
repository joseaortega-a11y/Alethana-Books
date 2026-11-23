<?php
/** Pasarela de pago simulada (equivalente a Pago.fxml + PagoController). */
require __DIR__ . '/../bootstrap.php';

use Alethana\Modelo\DetalleVenta;
use Alethana\Modelo\Venta;
use Alethana\Persistence\VentaRepository;
use Alethana\Service\CarritoService;
use Alethana\Util\Sesion;

$usuario        = Sesion::requerirUsuario();
$carritoService = new CarritoService();
$carrito        = $carritoService->getCarrito();

if (!isset($_SESSION['pago']) || $carrito->estaVacio()) {
    redirigir('carrito.php');
}
$metodo = $_SESSION['pago']['metodo'];
$_SESSION['pago']['efecty'] ??= sprintf('%08d', random_int(0, 99_999_999));

// El total siempre se recalcula en el servidor (no se confía en datos del navegador)
$estrategia = $carritoService->obtenerEstrategia($carrito);
$total      = round($estrategia->aplicar($carrito->calcularTotal()), 2);

$bancos = ['Bancolombia', 'Davivienda', 'BBVA', 'Banco de Bogotá', 'Nequi', 'Banco Popular', 'Itaú', 'Scotiabank Colpatria'];
$error  = null;

function validarPago(string $metodo, array $datos, array $bancos): ?string
{
    if ($metodo === 'Tarjeta') {
        $num = preg_replace('/\s+/', '', $datos['numero'] ?? '');
        if (!preg_match('/^\d{16}$/', $num))                                return 'El número de tarjeta debe tener 16 dígitos.';
        if (trim($datos['titular'] ?? '') === '')                          return 'Ingresa el nombre del titular.';
        if (!preg_match('#^(0[1-9]|1[0-2])/\d{2}$#', trim($datos['vence'] ?? ''))) return 'Fecha de vencimiento inválida. Usa MM/AA (ej: 08/27)';
        if (!preg_match('/^\d{3,4}$/', trim($datos['cvv'] ?? '')))         return 'CVV inválido. Debe tener 3 o 4 dígitos.';
    } elseif ($metodo === 'PSE') {
        if (!in_array($datos['banco'] ?? '', $bancos, true))               return 'Selecciona tu banco.';
        if (!preg_match('/^\d{6,12}$/', trim($datos['cedula'] ?? '')))     return 'Ingresa un número de documento válido.';
    }
    return null;
}

if (esPost()) {
    verificarCsrf();
    if (($_POST['accion'] ?? '') === 'cancelar') {
        unset($_SESSION['pago']);
        redirigir('carrito.php');
    }

    $error = validarPago($metodo, $_POST, $bancos);
    if ($error === null) {
        $aprobado = random_int(1, 100) > 10;   // 90% de probabilidad de aprobación (simulación)
        if (!$aprobado) {
            $error = 'Pago rechazado. Verifica tus datos e intenta de nuevo.';
        } else {
            $detalles = array_map(fn($item) => new DetalleVenta($item->getLibro(), $item->getCantidad()), $carrito->getItems());
            $venta = new Venta(null, $usuario, $detalles, $metodo, $total, 'PAGADO', $usuario->getDatosEnvio());
            $venta->setTipoDescuento($estrategia->getTipo());
            $venta->setCodigo($estrategia->getTipo() === 'PROMOCIONAL' ? $carritoService->getCodigoAplicado() : null);

            try {
                // El stock lo descuenta el trigger trg_detalle_venta_ai de la BD
                (new VentaRepository())->guardar($venta);
                $carritoService->vaciarCarrito();
                unset($_SESSION['pago']);
                flash('ok', "¡Compra completada! Tu pedido #{$venta->getId()} fue registrado y pagado. Método: {$metodo} · Total: " . cop($total)
                          . ($carrito->tieneDigitales() ? ' · Tus e-books ya están disponibles para descargar abajo.' : ''));
                redirigir('cuenta.php#compras');
            } catch (RuntimeException $e) {
                flash('error', 'No se pudo registrar la compra: ' . $e->getMessage());
                unset($_SESSION['pago']);
                redirigir('carrito.php');
            }
        }
    }
}

$titulo = 'Pasarela de pago';
require __DIR__ . '/../templates/header.php';
?>
<section class="panel pago">
    <h1>Pasarela de pago</h1>
    <dl class="totales">
        <dt>Método</dt><dd><?= e($metodo) ?></dd>
        <dt class="total">Total a pagar</dt><dd class="total"><?= cop($total) ?></dd>
    </dl>

    <form method="post" class="formulario" id="form-pago">
        <?= campoCsrf() ?>
        <input type="hidden" name="accion" value="pagar">

        <?php if ($metodo === 'Tarjeta'): ?>
            <label>Número de tarjeta
                <input name="numero" inputmode="numeric" maxlength="19" placeholder="1234 5678 9012 3456" value="<?= e($_POST['numero'] ?? '') ?>" required autocomplete="cc-number">
            </label>
            <label>Nombre del titular
                <input name="titular" value="<?= e($_POST['titular'] ?? '') ?>" required autocomplete="cc-name">
            </label>
            <div class="fila">
                <label>Vencimiento (MM/AA)
                    <input name="vence" maxlength="5" placeholder="08/27" value="<?= e($_POST['vence'] ?? '') ?>" required autocomplete="cc-exp">
                </label>
                <label>CVV
                    <input name="cvv" inputmode="numeric" maxlength="4" required autocomplete="cc-csc">
                </label>
            </div>
        <?php elseif ($metodo === 'PSE'): ?>
            <label>Banco
                <select name="banco" required>
                    <option value="">Seleccionar banco</option>
                    <?php foreach ($bancos as $b): ?>
                        <option <?= ($_POST['banco'] ?? '') === $b ? 'selected' : '' ?>><?= e($b) ?></option>
                    <?php endforeach; ?>
                </select>
            </label>
            <label>Número de documento
                <input name="cedula" inputmode="numeric" maxlength="12" value="<?= e($_POST['cedula'] ?? '') ?>" required>
            </label>
        <?php else: ?>
            <p>Presenta este código en cualquier punto Efecty o Baloto:</p>
            <p class="codigo-efecty"># <?= e($_SESSION['pago']['efecty']) ?></p>
        <?php endif; ?>

        <p class="estado-pago <?= $error ? 'estado-pago--error' : '' ?>" id="estado-pago" aria-live="polite"><?= e($error) ?></p>
        <div class="barra-progreso" id="barra-progreso" hidden><span></span></div>

        <div class="fila">
            <button type="submit" class="btn btn--degradado" id="btn-pagar">Pagar <?= cop($total) ?></button>
            <button type="submit" name="accion" value="cancelar" class="btn btn--secundario" formnovalidate>Cancelar</button>
        </div>
    </form>
</section>
<?php require __DIR__ . '/../templates/footer.php'; ?>
