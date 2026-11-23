<?php
/**
 * Punto de arranque común: autoload de clases (PSR-4 simple), sesión y manejo de errores.
 * Cada página de public/ hace: require __DIR__ . '/../bootstrap.php';
 */
declare(strict_types=1);

mb_internal_encoding('UTF-8');
date_default_timezone_set('America/Bogota');

spl_autoload_register(function (string $clase): void {
    $prefijo = 'Alethana\\';
    if (!str_starts_with($clase, $prefijo)) return;
    $ruta = __DIR__ . '/src/' . str_replace('\\', '/', substr($clase, strlen($prefijo))) . '.php';
    if (is_file($ruta)) require $ruta;
});

session_start([
    'cookie_httponly' => true,
    'cookie_samesite' => 'Lax',
]);

set_exception_handler(function (Throwable $e): void {
    http_response_code(500);
    error_log((string) $e);
    $esBD = $e instanceof PDOException;
    echo '<!doctype html><meta charset="utf-8"><title>Error</title>'
       . '<div style="font-family:sans-serif;max-width:640px;margin:60px auto;padding:24px;border:1px solid #fecaca;border-radius:12px;background:#fef2f2">'
       . '<h2 style="color:#b91c1c;margin-top:0">Ocurrió un error</h2><p>'
       . ($esBD ? 'No se pudo conectar con la base de datos. Verifica que MySQL esté encendido en XAMPP y que la base <b>alethana_books</b> exista.'
                : htmlspecialchars($e->getMessage(), ENT_QUOTES, 'UTF-8'))
       . '</p><a href="index.php">Volver al inicio</a></div>';
});

require __DIR__ . '/src/Util/funciones.php';
