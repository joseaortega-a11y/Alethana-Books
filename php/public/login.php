<?php
/** Login / registro (equivalente a Login.fxml + LoginController). */
require __DIR__ . '/../bootstrap.php';

use Alethana\Service\UsuarioService;
use Alethana\Util\Sesion;

$usuarioService = new UsuarioService();
$usuarioService->inicializarAdminPorDefecto();

$correo = trim($_POST['correo'] ?? '');
$apodo  = trim($_POST['apodo'] ?? '');
$error  = null;

if (esPost()) {
    verificarCsrf();
    $contra = (string) ($_POST['contrasena'] ?? '');

    if (($_POST['tipo'] ?? '') === 'admin') {
        if ($correo === '' || $contra === '') {
            $error = 'Ingresa correo y contraseña de administrador.';
        } elseif (!$usuarioService->esAdmin($correo, $contra)) {
            $error = 'Credenciales incorrectas o no eres administrador.';
        } else {
            Sesion::iniciar($usuarioService->autenticar($correo, $contra));
            redirigir('admin.php');
        }
    } else {
        if ($correo === '' || $apodo === '' || $contra === '') {
            $error = 'Por favor completa correo, nombre y contraseña.';
        } elseif (!filter_var($correo, FILTER_VALIDATE_EMAIL)) {
            $error = 'El correo no es válido.';
        } else {
            // Si el usuario no existe, se registra automáticamente
            $usuario = $usuarioService->autenticar($correo, $contra);
            if ($usuario === null) {
                $usuarioService->registrar($apodo, $correo, $contra);
                $usuario = $usuarioService->autenticar($correo, $contra);
            }
            if ($usuario === null) {
                $error = 'No se pudo autenticar. Verifica tu contraseña.';
            } else {
                Sesion::iniciar($usuario);
                redirigir('index.php');
            }
        }
    }
}
$flashes = obtenerFlashes();
?>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Ingresar · Alethana Books</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body class="pagina-login">
<main class="login">
    <img class="login__ilustracion" src="imagenes/undraw_fingerprint-login.png" alt="" onerror="this.remove()">
    <h1 class="logo">Alethana <span>Books</span></h1>
    <p class="login__texto">Bienvenido(a) a Alethana Books, nuestra tienda de libros online. Por favor ingresa tus datos.</p>

    <?php foreach ($flashes as $f): ?>
        <div class="aviso aviso--<?= e($f['tipo']) ?>"><?= e($f['mensaje']) ?></div>
    <?php endforeach; ?>
    <?php if ($error): ?><div class="aviso aviso--error"><?= e($error) ?></div><?php endif; ?>

    <form method="post" class="formulario">
        <?= campoCsrf() ?>
        <label>Dirección de correo:
            <input type="email" name="correo" value="<?= e($correo) ?>" required autocomplete="email">
        </label>
        <label>Nombre y apellidos:
            <input type="text" name="apodo" value="<?= e($apodo) ?>" maxlength="50" autocomplete="name">
        </label>
        <label>Contraseña:
            <input type="password" name="contrasena" required autocomplete="current-password">
        </label>
        <button type="submit" name="tipo" value="usuario" class="btn btn--bloque">Ingresar</button>
        <div class="separador">o</div>
        <button type="submit" name="tipo" value="admin" class="btn btn--secundario btn--bloque">Admin</button>
    </form>
    <p class="login__legal">Al continuar aceptas nuestros Términos de Servicio y Política de Privacidad</p>
</main>
</body>
</html>
