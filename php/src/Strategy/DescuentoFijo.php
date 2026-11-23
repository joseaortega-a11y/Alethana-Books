<?php
namespace Alethana\Strategy;

class DescuentoFijo implements EstrategiaDescuento
{
    public function __construct(private float $monto, private string $descripcion) {}

    public function aplicar(float $precio): float { return max(0, $precio - $this->monto); }
    public function getPorcentaje(): float        { return 0.0; }
    public function getDescripcion(): string
    {
        return $this->descripcion . ' (−COP ' . number_format($this->monto, 0, ',', '.') . ')';
    }
    public function getTipo(): string             { return 'FIJO'; }
}
