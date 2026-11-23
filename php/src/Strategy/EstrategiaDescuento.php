<?php
namespace Alethana\Strategy;

interface EstrategiaDescuento
{
    public function aplicar(float $precio): float;
    public function getPorcentaje(): float;
    public function getDescripcion(): string;
    /** Valor que se guarda en venta.tipo_descuento */
    public function getTipo(): string;
}
