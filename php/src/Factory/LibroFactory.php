<?php
namespace Alethana\Factory;

use Alethana\Modelo\Libro;
use Alethana\Modelo\LibroDigital;
use Alethana\Modelo\LibroFisico;

/** Factory Method: decide qué subclase de Libro crear según el tipo. */
class LibroFactory
{
    public const FISICO  = 'FISICO';
    public const DIGITAL = 'DIGITAL';

    public static function crearLibro(
        string $tipo, ?string $id, string $titulo, string $autor, string $categoria,
        float $precio, int $stock, ?string $imagen,
        ?string $formato = null, ?string $rutaArchivo = null, ?string $origen = 'Nacional'
    ): Libro {
        return match ($tipo) {
            self::FISICO  => new LibroFisico($id, $titulo, $autor, $categoria, $precio, $stock, $imagen,
                                             0.4, 'Bodega A', $origen ?? 'Nacional'),
            self::DIGITAL => new LibroDigital($id, $titulo, $autor, $categoria, $precio, $stock, $imagen,
                                              $rutaArchivo, $rutaArchivo !== null && trim($rutaArchivo) !== '', $formato),
            default       => throw new \InvalidArgumentException("Tipo de libro desconocido: $tipo"),
        };
    }
}
