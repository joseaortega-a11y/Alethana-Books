<?php
/** Mi cuenta: datos de envío e historial de compras (equivalente a Cuenta.fxml + CuentaController). */
require __DIR__ . '/../bootstrap.php';

use Alethana\Modelo\DatosEnvio;
use Alethana\Modelo\LibroDigital;
use Alethana\Persistence\VentaRepository;
use Alethana\Service\CatalogoService;
use Alethana\Service\UsuarioService;
use Alethana\Util\Sesion;

$usuario = Sesion::requerirUsuario();
$envio   = $usuario->getDatosEnvio();
$campos  = [
    'nombre'    => $envio?->getNombre() ?? $usuario->getNombre(),
    'direccion' => $envio?->getDireccion() ?? '',
    'ciudad'    => $envio?->getCiudad() ?? '',
    'telefono'  => $envio?->getTelefono() ?? '',
];

if (esPost()) {
    verificarCsrf();
    foreach ($campos as $clave => $_) {
        $campos[$clave] = trim($_POST[$clave] ?? '');
    }
    if (in_array('', $campos, true)) {
        flash('error', 'Completa todos los campos de envío.');
    } elseif (!preg_match('/^\d{7,15}$/', $campos['telefono'])) {
        flash('error', 'El teléfono debe tener solo dígitos (7-15).');
    } elseif ($usuario->esAdmin()) {
        flash('error', 'Los administradores no tienen datos de envío.');
    } else {
        $usuario->setDatosEnvio(new DatosEnvio($campos['nombre'], $campos['direccion'], $campos['ciudad'], $campos['telefono']));
        try {
            (new UsuarioService())->guardarDatosEnvio($usuario);
            flash('ok', 'Datos de envío guardados correctamente.');
            redirigir('cuenta.php');
        } catch (PDOException) {
            flash('error', 'No se pudieron guardar los datos de envío.');
        }
    }
}

$ventas = (new VentaRepository())->obtenerPorUsuario($usuario->getId());
// Para saber qué libros comprados son digitales (y mostrar su enlace de descarga)
$digitales = [];
foreach ((new CatalogoService())->obtenerTodos() as $libro) {
    if ($libro instanceof LibroDigital && $libro->estaDisponibleParaDescarga()) $digitales[$libro->getId()] = $libro;
}

$titulo = 'Mi cuenta';
require __DIR__ . '/../templates/header.php';
?>
<h1>Mi cuenta</h1>
<p class="subtitulo">Hola, <?= e($usuario->getNombre()) ?> · <?= e($usuario->getCorreo()) ?></p>

<div class="dos-columnas">
    <form method="post" class="panel formulario">
        <?= campoCsrf() ?>
        <h2>Datos de envío</h2>
        <label>Nombre completo <input name="nombre" maxlength="100" value="<?= e($campos['nombre']) ?>" required></label>
        <label>Dirección <input name="direccion" maxlength="150" value="<?= e($campos['direccion']) ?>" required></label>
        <label>Ciudad <input name="ciudad" maxlength="60" value="<?= e($campos['ciudad']) ?>" required></label>
        <label>Teléfono <input name="telefono" inputmode="numeric" pattern="\d{7,15}" maxlength="15" value="<?= e($campos['telefono']) ?>" required></label>
        <button class="btn">Guardar datos de envío</button>
    </form>

    <section id="compras">
        <h2>Historial de compras</h2>
        <?php if ($ventas === []): ?>
            <p class="vacio">Aún no tienes compras registradas.</p>
        <?php endif; ?>
        <div class="lista">
        <?php foreach ($ventas as $v): ?>
            <article class="panel compra">
                <header>
                    <strong>Pedido #<?= e($v->getId()) ?></strong>
                    <span><?= $v->getFecha()->format('d/m/Y H:i') ?> · <?= e($v->getMetodoPago()) ?></span>
                </header>
                <ul>
                <?php foreach ($v->getDetalles() as $d): $idLibro = $d->getLibro()->getId(); ?>
                    <li>
                        <?= e($d->getLibro()->getTitulo()) ?> ×<?= $d->getCantidad() ?> → <?= cop($d->getSubtotal()) ?>
                        <?php if (isset($digitales[$idLibro])): ?>
                            <a class="enlace-descarga" href="descargar.php?id=<?= e($idLibro) ?>">⬇ Descargar <?= e($digitales[$idLibro]->getFormato()) ?></a>
                        <?php endif; ?>
                    </li>
                <?php endforeach; ?>
                </ul>
                <?php if ($v->getDatosEnvio()->estaCompleto()): ?>
                    <p class="tarjeta__autor">Envío: <?= e($v->getDatosEnvio()->getDireccion()) ?>, <?= e($v->getDatosEnvio()->getCiudad()) ?></p>
                <?php endif; ?>
                <?php if ($v->getDescuento() > 0): ?>
                    <p class="nota-verde">Descuento <?= e(strtolower($v->getTipoDescuento())) ?><?= $v->getCodigo() ? ' (' . e($v->getCodigo()) . ')' : '' ?>: - <?= cop($v->getDescuento()) ?></p>
                <?php endif; ?>
                <p class="precio">Total: <?= cop($v->getTotal()) ?></p>
            </article>
        <?php endforeach; ?>
        </div>
    </section>
</div>
<?php require __DIR__ . '/../templates/footer.php'; ?>
