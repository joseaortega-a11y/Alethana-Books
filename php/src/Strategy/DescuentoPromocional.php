<?php
namespace Alethana\Strategy;

class DescuentoPromocional implements EstrategiaDescuento
{
    public function __construct(private float $porcentaje, private string $codigo = 'PROMO') {}

    public function aplicar(float $precio): float { return $precio - ($precio * $this->porcentaje); }
    public function getPorcentaje(): float        { return $this->porcentaje; }
    public function getDescripcion(): string
    {
        return 'Código ' . $this->codigo . ' (−' . (int) round($this->porcentaje * 100) . '%)';
    }
    public function getTipo(): string             { return 'PROMOCIONAL'; }
    public function getCodigo(): string           { return $this->codigo; }
}
