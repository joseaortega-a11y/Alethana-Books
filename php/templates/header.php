<?php
/**
 * Encabezado común (equivalente a header.fxml + HeaderController).
 * Variables esperadas: $titulo (string), $activo (string: clave del menú).
 */
use Alethana\Service\CarritoService;
use Alethana\Util\Sesion;

$usuarioHeader = Sesion::getUsuario();
$itemsCarrito  = $usuarioHeader ? (new CarritoService())->contarItems() : 0;
$activo      ??= '';
$menu = [
    'inicio'       => ['index.php',        'Inicio'],
    'recomendados' => ['recomendados.php', 'Recomendados'],
    'ultimos'      => ['ultimos.php',      'Últimos añadidos'],
    'importados'   => ['importados.php',   'Libros importados'],
    'digitales'    => ['digitales.php',    'Biblioteca Digital'],
    'opiniones'    => ['opiniones.php',    'Opiniones'],
];
?>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><?= e($titulo ?? 'Alethana Books') ?> · Alethana Books</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
<header class="topbar">
    <div class="topbar__fila">
        <a class="logo" href="index.php">Alethana <span>Books</span></a>

        <form class="buscador" action="index.php" method="get" role="search">
            <input type="search" name="q" placeholder="Buscar por título, autor o categoría"
                   value="<?= e($_GET['q'] ?? '') ?>" aria-label="Buscar libros">
            <button type="submit">Buscar</button>
        </form>

        <nav class="acciones">
            <a href="carrito.php" class="accion">🛒 Carrito<?php if ($itemsCarrito > 0): ?> <span class="contador"><?= $itemsCarrito ?></span><?php endif; ?></a>
            <a href="cuenta.php" class="accion">👤 Mi Cuenta</a>
            <button type="button" class="accion" data-abrir="dialogo-ayuda">❓ Ayuda</button>
            <?php if ($usuarioHeader): ?>
                <a href="logout.php" class="accion accion--salir">Cerrar sesión</a>
            <?php endif; ?>
        </nav>
    </div>
    <nav class="menu">
        <?php foreach ($menu as $clave => [$url, $texto]): ?>
            <a href="<?= $url ?>" class="<?= $clave === $activo ? 'activo' : '' ?>"><?= e($texto) ?></a>
        <?php endforeach; ?>
        <span class="menu__envio">🇨🇴 Enviar a Colombia</span>
    </nav>
</header>

<dialog id="dialogo-ayuda" class="dialogo">
    <h2>Centro de ayuda — Preguntas frecuentes</h2>
    <dl>
        <dt>¿Cómo compro un libro?</dt>
        <dd>Agrégalo al carrito y confirma la compra en el carrito.</dd>
        <dt>¿Qué métodos de pago hay?</dt>
        <dd>Tarjeta crédito/débito, PSE y Efecty/Baloto.</dd>
        <dt>¿Qué códigos promocionales hay?</dt>
        <dd>Usa estos códigos promocionales ;): <b>CYBER20</b>, <b>PROMO10</b> o <b>LECTOR15</b>.</dd>
        <dt>¿Cómo descargo los libros digitales?</dt>
        <dd>Después de comprar aparece el enlace de descarga en tu cuenta.</dd>
        <dt>¿Cómo uso un código de descuento?</dt>
        <dd>Escríbelo en el campo «Código de descuento» dentro del carrito.</dd>
    </dl>
    <form method="dialog"><button class="btn">Entendido</button></form>
</dialog>

<main class="contenido">
<?php foreach (obtenerFlashes() as $f): ?>
    <div class="aviso aviso--<?= e($f['tipo']) ?>"><?= e($f['mensaje']) ?></div>
<?php endforeach; ?>
