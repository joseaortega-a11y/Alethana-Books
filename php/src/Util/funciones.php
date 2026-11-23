<?php
/** Funciones de ayuda para las vistas (escapar HTML, formato de precios, mensajes, CSRF...). */

use Alethana\Modelo\Libro;

/** Escapa texto para mostrarlo en HTML de forma segura. */
function e(?string $texto): string
{
    return htmlspecialchars($texto ?? '', ENT_QUOTES, 'UTF-8');
}

/** Formato de precio: COP 29.000 */
function cop(float $valor): string
{
    return 'COP ' . number_format($valor, 0, ',', '.');
}

function redirigir(string $url): never
{
    header('Location: ' . $url);
    exit;
}

/** Solo permite volver a páginas internas (evita redirecciones abiertas). */
function urlInternaSegura(?string $url, string $porDefecto = 'index.php'): string
{
    if ($url === null || $url === '' || !preg_match('#^[a-zA-Z0-9_\-]+\.php(\?[^\s]*)?$#', $url)) {
        return $porDefecto;
    }
    return $url;
}

/** Mensajes de una sola vez (equivalente a los Alert de JavaFX). */
function flash(string $tipo, string $mensaje): void
{
    $_SESSION['flash'][] = ['tipo' => $tipo, 'mensaje' => $mensaje];
}

function obtenerFlashes(): array
{
    $mensajes = $_SESSION['flash'] ?? [];
    unset($_SESSION['flash']);
    return $mensajes;
}

function tokenCsrf(): string
{
    return $_SESSION['csrf'] ??= bin2hex(random_bytes(16));
}

function campoCsrf(): string
{
    return '<input type="hidden" name="csrf" value="' . tokenCsrf() . '">';
}

function verificarCsrf(): void
{
    if (!hash_equals(tokenCsrf(), (string) ($_POST['csrf'] ?? ''))) {
        http_response_code(400);
        exit('Solicitud inválida (token CSRF). Recarga la página e intenta de nuevo.');
    }
}

function esPost(): bool
{
    return $_SERVER['REQUEST_METHOD'] === 'POST';
}

/** URL pública de una portada; codifica cada segmento (hay nombres con espacios). */
function urlImagen(?string $ruta): ?string
{
    if ($ruta === null || trim($ruta) === '') return null;
    $ruta = str_replace('\\', '/', $ruta);
    if (str_contains($ruta, '..')) return null;
    $archivo = __DIR__ . '/../../public/imagenes/' . $ruta;
    if (!is_file($archivo)) return null;
    return 'imagenes/' . implode('/', array_map('rawurlencode', explode('/', $ruta)));
}

/** URL de la página actual (para volver después de agregar al carrito). */
function urlActual(): string
{
    $pagina = basename($_SERVER['SCRIPT_NAME']);
    $query  = $_SERVER['QUERY_STRING'] ?? '';
    return $pagina . ($query !== '' ? '?' . $query : '');
}
