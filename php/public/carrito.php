<?php
/** Carrito de compras (equivalente a Carrito.fxml + CarritoController). */
require __DIR__ . '/../bootstrap.php';
require __DIR__ . '/../templates/componentes.php';

use Alethana\Service\CarritoService;
use Alethana\Util\Sesion;

$usuario        = Sesion::requerirUsuario();
$carritoService = new CarritoService();
$metodos        = ['Tarjeta' => '💳 Tarjeta crédito/débito', 'PSE' => '🏦 PSE', 'Efecty' => '💵 Efecty / Baloto'];

if (esPost()) {
    verificarCsrf();
    $accion = $_POST['accion'] ?? '';

    switch ($accion) {
        case 'agregar':
            $volver = urlInternaSegura($_POST['volver'] ?? null);
            try {
                $tituloLibro = $carritoService->agregarLibro((string) ($_POST['id_libro'] ?? ''), 1);
                flash('ok', "«{$tituloLibro}» agregado al carrito.");
            } catch (InvalidArgumentException $e) {
                flash('error', $e->getMessage());
            }
            redirigir($volver);

        case 'quitar':
            $carritoService->eliminarLibro((string) ($_POST['id_libro'] ?? ''));
            redirigir('carrito.php');

        case 'codigo':
            $porcentaje = $carritoService->aplicarCodigo((string) ($_POST['codigo'] ?? ''));
            $porcentaje === null
                ? flash('error', 'Código no válido.')
                : flash('ok', "Descuento del {$porcentaje}% aplicado.");
            redirigir('carrito.php');

        case 'confirmar':
            $carrito = $carritoService->getCarrito();
            if ($carrito->estaVacio()) {
                flash('error', 'Agrega libros antes de confirmar la compra.');
                redirigir('carrito.php');
            }
            if (!$usuario->getDatosEnvio()?->estaCompleto()) {
                flash('error', 'Debes completar tu dirección de envío antes de comprar. Llena los campos de envío en «Mi cuenta».');
                redirigir('cuenta.php');
            }
            foreach ($carrito->getItems() as $item) {
                if ($item->getLibro()->getStock() < $item->getCantidad()) {
                    flash('error', 'No hay suficiente stock de: ' . $item->getLibro()->getTitulo()
                                   . '. Disponible: ' . $item->getLibro()->getStock());
                    redirigir('carrito.php');
                }
            }
            $metodo = array_key_exists($_POST['metodo'] ?? '', $metodos) ? $_POST['metodo'] : 'Tarjeta';
            $_SESSION['pago'] = ['metodo' => $metodo];
            redirigir('pago.php');
    }
    redirigir('carrito.php');
}

$carrito    = $carritoService->getCarrito();
$estrategia = $carritoService->obtenerEstrategia($carrito);
$subtotal   = $carrito->calcularTotal();
$total      = $estrategia->aplicar($subtotal);
$descuento  = $subtotal - $total;

$titulo = 'Mi carrito';
require __DIR__ . '/../templates/header.php';
?>
<h1>Mi carrito</h1>
<div class="dos-columnas">
    <section class="lista">
        <?php if ($carrito->estaVacio()): ?>
            <p class="vacio">Tu carrito está vacío. <a href="index.php">Ir al catálogo</a></p>
        <?php endif; ?>
        <?php foreach ($carrito->getItems() as $item): $libro = $item->getLibro(); ?>
            <article class="panel item-carrito">
                <?= portada($libro, 'portada portada--mini') ?>
                <div class="item-carrito__info">
                    <h3><?= e($libro->getTitulo()) ?></h3>
                    <p class="tarjeta__autor"><?= e($libro->getAutor()) ?></p>
                    <p class="tarjeta__autor">Cantidad: <?= $item->getCantidad() ?> · <?= cop($libro->getPrecio()) ?> c/u</p>
                    <?php if ($libro->getStock() < $item->getCantidad()): ?>
                        <p class="stock stock--no">Solo quedan <?= $libro->getStock() ?> unidades</p>
                    <?php endif; ?>
                </div>
                <strong class="precio"><?= cop($item->calcularSubtotal()) ?></strong>
                <form method="post">
                    <?= campoCsrf() ?>
                    <input type="hidden" name="accion" value="quitar">
                    <input type="hidden" name="id_libro" value="<?= e($libro->getId()) ?>">
                    <button class="btn-quitar" title="Quitar del carrito" aria-label="Quitar <?= e($libro->getTitulo()) ?>">✕</button>
                </form>
            </article>
        <?php endforeach; ?>
    </section>

    <aside class="panel resumen">
        <h2>Resumen</h2>
        <form method="post" class="codigo">
            <?= campoCsrf() ?>
            <input type="hidden" name="accion" value="codigo">
            <label for="codigo">Código de descuento</label>
            <div class="fila">
                <input id="codigo" name="codigo" value="<?= e($carritoService->getCodigoAplicado()) ?>" placeholder="Ej: CYBER20">
                <button class="btn btn--secundario">Aplicar</button>
            </div>
        </form>
        <p class="nota-verde"><?= e($estrategia->getDescripcion()) ?></p>

        <dl class="totales">
            <dt>Subtotal</dt><dd><?= cop($subtotal) ?></dd>
            <dt>Descuento</dt><dd>- <?= cop($descuento) ?></dd>
            <dt class="total">Total</dt><dd class="total"><?= cop($total) ?></dd>
        </dl>

        <form method="post">
            <?= campoCsrf() ?>
            <input type="hidden" name="accion" value="confirmar">
            <fieldset class="metodos">
                <legend>Método de pago</legend>
                <?php foreach ($metodos as $valor => $texto): ?>
                    <label><input type="radio" name="metodo" value="<?= $valor ?>" <?= $valor === 'Tarjeta' ? 'checked' : '' ?>> <?= $texto ?></label>
                <?php endforeach; ?>
            </fieldset>
            <button class="btn btn--bloque btn--degradado" <?= $carrito->estaVacio() ? 'disabled' : '' ?>>Confirmar compra</button>
        </form>
    </aside>
</div>
<?php require __DIR__ . '/../templates/footer.php'; ?>
