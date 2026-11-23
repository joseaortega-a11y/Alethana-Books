<?php
/** Los 10 libros más recientes (equivalente a UltimosAnadidos.fxml + UltimosAnadidosController). */
require __DIR__ . '/../bootstrap.php';
require __DIR__ . '/../templates/componentes.php';

use Alethana\Service\CatalogoService;
use Alethana\Util\Sesion;

Sesion::requerirUsuario();
$libros  = (new CatalogoService())->obtenerUltimos(10);
$colores = ['#7c3aed', '#ec4899', '#10b981', '#f59e0b', '#3b82f6', '#0ea574', '#ef4444', '#6366f1', '#f59e0b', '#94a3b8'];

$titulo = 'Últimos añadidos';
$activo = 'ultimos';
require __DIR__ . '/../templates/header.php';
?>
<h1>Últimos añadidos</h1>
<p class="subtitulo">Los 10 libros más recientes en nuestro catálogo.</p>

<?php if ($libros === []): ?>
    <p class="vacio">No hay libros en el catálogo aún.</p>
<?php endif; ?>

<div class="lista-ranking">
<?php foreach ($libros as $i => $libro): ?>
    <article class="fila-ranking">
        <span class="fila-ranking__num" style="color: <?= $colores[$i % count($colores)] ?>">#<?= $i + 1 ?></span>
        <?= portada($libro, 'portada portada--mini') ?>
        <div class="fila-ranking__info">
            <h3><?= e($libro->getTitulo()) ?></h3>
            <p class="tarjeta__autor"><?= e($libro->getAutor()) ?></p>
            <span class="chip"><?= e($libro->getCategoria()) ?></span>
            <p class="stock <?= $libro->hayStock() ? 'stock--ok' : 'stock--no' ?>">
                <?= $libro->hayStock() ? 'Stock: ' . $libro->getStock() : 'Sin stock' ?>
            </p>
        </div>
        <div class="fila-ranking__acciones">
            <p class="precio"><?= cop($libro->getPrecio()) ?></p>
            <?= botonCarrito($libro, 'btn') ?>
        </div>
    </article>
<?php endforeach; ?>
</div>
<?php require __DIR__ . '/../templates/footer.php'; ?>
