<?php
/** Catálogo principal (equivalente a alethana-books.fxml + InicioController). */
require __DIR__ . '/../bootstrap.php';
require __DIR__ . '/../templates/componentes.php';

use Alethana\Service\CatalogoService;
use Alethana\Util\Sesion;

Sesion::requerirUsuario();

$catalogo   = new CatalogoService();
$todos      = $catalogo->obtenerTodos();
$categorias = $catalogo->obtenerCategorias();
$categoria  = $_GET['categoria'] ?? null;
$busqueda   = trim($_GET['q'] ?? '');

if ($busqueda !== '') {
    $libros = $catalogo->buscar($busqueda);
    $subtitulo = 'Resultados para «' . $busqueda . '» (' . count($libros) . ')';
} elseif ($categoria !== null && $categoria !== '') {
    $libros = $catalogo->filtrarPorCategoria($categoria);
    $subtitulo = $categoria;
} else {
    $libros = $todos;
    $subtitulo = 'Todas las categorías';
}

$banner   = array_slice($todos, 0, 3);
$novedad  = $todos === [] ? null : end($todos);
$coloresBanner = ['#1e293b', '#33fdcb', '#be185d'];

$titulo = 'Catálogo';
$activo = 'inicio';
require __DIR__ . '/../templates/header.php';
?>
<section class="hero">
    <div>
        <p class="hero__etiqueta">Cyber Days</p>
        <h1>Hasta 20% OFF</h1>
        <p>con códigos promocionales · Bienvenido, disfruta de nuestra app de libros Alethana Books</p>
    </div>
    <div class="hero__portadas">
        <?php foreach ($banner as $i => $libro): ?>
            <div class="hero__portada" style="background: <?= $coloresBanner[$i] ?>">
                <?= portada($libro) ?>
                <span><?= e($libro->getTitulo()) ?></span>
            </div>
        <?php endforeach; ?>
    </div>
</section>

<div class="tres-columnas">
    <aside class="lateral">
        <h2>Categorías</h2>
        <a href="index.php" class="categoria <?= ($categoria === null && $busqueda === '') ? 'activa' : '' ?>">Todas las categorías</a>
        <?php foreach ($categorias as $cat): ?>
            <a href="index.php?categoria=<?= urlencode($cat) ?>" class="categoria <?= $cat === $categoria ? 'activa' : '' ?>"><?= e($cat) ?></a>
        <?php endforeach; ?>
    </aside>

    <section>
        <h2>Categorías / Resultados de búsqueda</h2>
        <p class="subtitulo"><?= e($subtitulo) ?></p>
        <?= grillaLibros($libros, 'No hay libros disponibles.', [], 'grilla grilla--3') ?>
    </section>

    <aside class="lateral novedad">
        <h2>Novedades en libros</h2>
        <?php if ($novedad === null): ?>
            <p class="vacio">Aún no hay novedades.</p>
        <?php else: ?>
            <?= portada($novedad, 'portada portada--grande') ?>
            <h3><?= e($novedad->getTitulo()) ?></h3>
            <p class="tarjeta__autor"><?= e($novedad->getAutor()) ?></p>
            <p class="precio precio--grande"><?= cop($novedad->getPrecio()) ?></p>
            <span class="chip chip--ambar"><?= e($novedad->getCategoria()) ?></span>
            <p class="stock <?= $novedad->hayStock() ? 'stock--ok' : 'stock--no' ?>">Stock: <?= $novedad->getStock() ?></p>
            <?= botonCarrito($novedad, 'btn btn--bloque btn--degradado') ?>
        <?php endif; ?>
    </aside>
</div>
<?php require __DIR__ . '/../templates/footer.php'; ?>
