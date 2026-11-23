<?php
/** Biblioteca digital con filtro por formato (equivalente a LibrosDigitales.fxml + LibrosDigitalesController). */
require __DIR__ . '/../bootstrap.php';
require __DIR__ . '/../templates/componentes.php';

use Alethana\Service\CatalogoService;
use Alethana\Util\Sesion;

Sesion::requerirUsuario();

$formatos = ['PDF', 'EPUB', 'MOBI'];
$formato  = in_array($_GET['formato'] ?? '', $formatos, true) ? $_GET['formato'] : null;
$digitales = (new CatalogoService())->obtenerDigitales($formato);
$n = count($digitales);

$titulo = 'Biblioteca Digital';
$activo = 'digitales';
require __DIR__ . '/../templates/header.php';
?>
<section class="hero hero--digital">
    <div>
        <h1>📥 Biblioteca Digital</h1>
        <p>Descarga instantánea · PDF · EPUB · MOBI · 10% de descuento automático en todos los e-books</p>
    </div>
</section>

<div class="filtros">
    <span>Filtrar por formato:</span>
    <a href="digitales.php" class="filtro <?= $formato === null ? 'activo' : '' ?>">Todos</a>
    <?php foreach ($formatos as $f): ?>
        <a href="digitales.php?formato=<?= $f ?>" class="filtro <?= $formato === $f ? 'activo' : '' ?>"><?= $f ?></a>
    <?php endforeach; ?>
    <span class="filtros__conteo"><?= $n === 0 ? 'No hay e-books disponibles' : $n . ' e-book' . ($n === 1 ? '' : 's') . ' disponibles' ?></span>
</div>

<?= grillaLibros($digitales, 'No hay libros digitales con ese formato aún.', ['precioDigital' => true], 'grilla grilla--5') ?>
<?php require __DIR__ . '/../templates/footer.php'; ?>
