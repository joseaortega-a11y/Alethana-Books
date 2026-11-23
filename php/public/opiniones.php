<?php
/** Opiniones de clientes (equivalente a Opiniones.fxml + OpinionController). */
require __DIR__ . '/../bootstrap.php';

use Alethana\Modelo\Opinion;
use Alethana\Persistence\OpinionRepository;
use Alethana\Util\Sesion;

$usuario = Sesion::requerirUsuario();
$repo    = new OpinionRepository();
$texto   = '';
$estrellas = 3;

if (esPost()) {
    verificarCsrf();
    $texto     = trim($_POST['texto'] ?? '');
    $estrellas = max(1, min(5, (int) ($_POST['estrellas'] ?? 3)));

    if ($texto === '') {
        flash('error', 'Escribe tu opinión antes de publicar.');
    } elseif (mb_strlen($texto) < 10) {
        flash('error', 'La opinión debe tener al menos 10 caracteres.');
    } elseif ($usuario->esAdmin()) {
        flash('error', 'Las opiniones solo pueden publicarlas los clientes.');
    } else {
        try {
            $repo->agregar(new Opinion(null, $usuario->getId(), $usuario->getNombre(), $texto, $estrellas));
            flash('ok', 'Opinión publicada. ¡Gracias!');
            redirigir('opiniones.php');   // patrón Post/Redirect/Get
        } catch (PDOException) {
            flash('error', 'No se pudo publicar la opinión.');
        }
    }
}

$opiniones = $repo->obtenerTodas();
$titulo = 'Opiniones';
$activo = 'opiniones';
require __DIR__ . '/../templates/header.php';
?>
<h1>Opiniones de clientes</h1>

<form method="post" class="panel formulario">
    <?= campoCsrf() ?>
    <label>Tu opinión
        <textarea name="texto" rows="4" minlength="10" placeholder="Cuéntanos qué te pareció Alethana Books..." required><?= e($texto) ?></textarea>
    </label>
    <fieldset class="estrellas">
        <legend>Calificación</legend>
        <?php for ($i = 1; $i <= 5; $i++): ?>
            <label><input type="radio" name="estrellas" value="<?= $i ?>" <?= $i === $estrellas ? 'checked' : '' ?>> <?= str_repeat('★', $i) ?></label>
        <?php endfor; ?>
    </fieldset>
    <button type="submit" class="btn">Publicar opinión</button>
</form>

<?php if ($opiniones === []): ?>
    <p class="vacio">Sé el primero en dejar una opinión.</p>
<?php endif; ?>
<div class="lista">
<?php foreach ($opiniones as $op): ?>
    <article class="panel opinion">
        <header>
            <strong><?= e($op->getNombreUsuario()) ?></strong>
            <span class="estrellas-texto"><?= str_repeat('★', $op->getEstrellas()) . str_repeat('☆', 5 - $op->getEstrellas()) ?></span>
            <time><?= $op->getFecha()->format('d/m/Y H:i') ?></time>
        </header>
        <p><?= nl2br(e($op->getTexto())) ?></p>
    </article>
<?php endforeach; ?>
</div>
<?php require __DIR__ . '/../templates/footer.php'; ?>
