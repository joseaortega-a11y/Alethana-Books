<?php
namespace Alethana\Strategy;

class DescuentoNormal implements EstrategiaDescuento
{
    public function aplicar(float $precio): float { return $precio; }
    public function getPorcentaje(): float        { return 0.0; }
    public function getDescripcion(): string      { return 'Sin descuento'; }
    public function getTipo(): string             { return 'NORMAL'; }
}
