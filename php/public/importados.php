<?php
/** Libros físicos importados (equivalente a LibrosImportados.fxml + LibrosImportadosController). */
require __DIR__ . '/../bootstrap.php';
require __DIR__ . '/../templates/componentes.php';

use Alethana\Service\CatalogoService;
use Alethana\Util\Sesion;

Sesion::requerirUsuario();
$importados = (new CatalogoService())->obtenerImportados();

$titulo = 'Libros importados';
$activo = 'importados';
require __DIR__ . '/../templates/header.php';
?>
<section class="hero hero--importados">
    <div>
        <p class="hero__etiqueta">Colección Internacional 2026</p>
        <h1>Libros importados para ti</h1>
        <p>Ediciones internacionales seleccionadas especialmente para nuestros lectores.
           Más de 200 títulos importados directamente de Europa y Norteamérica.</p>
    </div>
</section>
<?= grillaLibros($importados, 'No hay libros importados disponibles aún.', ['etiqueta' => 'IMPORTADO'], 'grilla grilla--5') ?>
<?php require __DIR__ . '/../templates/footer.php'; ?>
