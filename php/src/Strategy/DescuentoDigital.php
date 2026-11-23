<?php
namespace Alethana\Strategy;

use Alethana\Interfaces\Descargable;
use Alethana\Modelo\ItemCarrito;

/** 10% de descuento sobre los e-books del carrito; los físicos pagan precio completo. */
class DescuentoDigital implements EstrategiaDescuento
{
    private const PORCENTAJE_DIGITAL = 0.10;

    /** @param ItemCarrito[] $items */
    public function __construct(private array $items) {}

    public function aplicar(float $precio): float
    {
        if ($this->items === []) return $precio;

        $total = 0.0;
        foreach ($this->items as $item) {
            $libro = $item->getLibro();
            $total += ($libro instanceof Descargable)
                ? $item->calcularSubtotal() * (1 - self::PORCENTAJE_DIGITAL)
                : $item->calcularSubtotal();
        }
        return $total;
    }

    public function getPorcentaje(): float   { return self::PORCENTAJE_DIGITAL; }
    public function getDescripcion(): string { return 'Descuento digital automático (−10% en e-books)'; }
    public function getTipo(): string        { return 'DIGITAL'; }
}
