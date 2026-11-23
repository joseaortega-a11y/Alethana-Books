<?php
namespace Alethana\Persistence;

use PDO;

/**
 * Conexión PDO a la base de datos MySQL/MariaDB de XAMPP.
 * Se reutiliza una sola conexión por petición HTTP.
 */
final class ConexionBD
{
    private static ?PDO $conexion = null;

    private function __construct() {}

    public static function obtener(): PDO
    {
        if (self::$conexion === null) {
            $cfg = (require __DIR__ . '/../../config/config.php')['db'];
            $dsn = "mysql:host={$cfg['host']};port={$cfg['puerto']};dbname={$cfg['nombre']};charset=utf8mb4";
            self::$conexion = new PDO($dsn, $cfg['usuario'], $cfg['clave'], [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES   => false,
            ]);
        }
        return self::$conexion;
    }
}
