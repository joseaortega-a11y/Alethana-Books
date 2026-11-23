<?php
/** Libros recomendados aleatorios (equivalente a librosRe.fxml + librosReControlador). */
require __DIR__ . '/../bootstrap.php';
require __DIR__ . '/../templates/componentes.php';

use Alethana\Service\CatalogoService;
use Alethana\Util\Sesion;

Sesion::requerirUsuario();
$recomendados = (new CatalogoService())->obtenerRecomendadosAleatorios(12);

$titulo = 'Recomendados';
$activo = 'recomendados';
require __DIR__ . '/../templates/header.php';
?>
<div class="encabezado-seccion">
    <div>
        <h1>Libros recomendados para ti</h1>
        <p class="subtitulo">Tu selección de hoy · Una selección especial generada aleatoriamente para ti.</p>
    </div>
    <a href="recomendados.php" class="btn">🎲 Generar recomendación</a>
</div>
<?= grillaLibros($recomendados, 'No hay libros disponibles aún. El administrador debe agregar libros primero.', [], 'grilla grilla--5') ?>
<?php require __DIR__ . '/../templates/footer.php'; ?>
