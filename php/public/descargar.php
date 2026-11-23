<?php
/** Descarga de e-books comprados (reemplaza el FileChooser de la versión Java). */
require __DIR__ . '/../bootstrap.php';

use Alethana\Interfaces\Descargable;
use Alethana\Persistence\VentaRepository;
use Alethana\Service\CatalogoService;
use Alethana\Util\Sesion;

$usuario = Sesion::requerirUsuario();
$id      = (string) ($_GET['id'] ?? '');
$libro   = (new CatalogoService())->buscarPorId($id);

if (!$libro instanceof Descargable || !$libro->estaDisponibleParaDescarga()) {
    flash('error', 'Ese libro no tiene archivo descargable.');
    redirigir('cuenta.php');
}
if (!$usuario->esAdmin() && !(new VentaRepository())->usuarioComproLibro($usuario->getId(), $id)) {
    flash('error', 'Solo puedes descargar los e-books que has comprado.');
    redirigir('cuenta.php');
}

// La ruta guardada es relativa a la carpeta "descargables" (fuera de public/)
$carpeta = realpath((require __DIR__ . '/../config/config.php')['carpeta_descargables']);
$archivo = $carpeta ? realpath($carpeta . DIRECTORY_SEPARATOR . $libro->obtenerRutaDescarga()) : false;

if ($archivo === false || !str_starts_with($archivo, $carpeta) || !is_file($archivo)) {
    flash('error', 'No se encontró el archivo descargable de: ' . $libro->getTitulo()
                   . '. Pide al administrador que lo suba desde el panel de administración.');
    redirigir('cuenta.php');
}

header('Content-Type: application/octet-stream');
header('Content-Length: ' . filesize($archivo));
header("Content-Disposition: attachment; filename*=UTF-8''" . rawurlencode(basename($archivo)));
header('X-Content-Type-Options: nosniff');
readfile($archivo);
