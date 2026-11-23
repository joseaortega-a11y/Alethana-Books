<?php
namespace Alethana\Modelo;

class DetalleVenta
{
    private float $subtotal;

    public function __construct(private Libro $libro, private int $cantidad)
    {
        $this->subtotal = round($libro->getPrecio() * $cantidad, 2);
    }

    public function getLibro(): Libro    { return $this->libro; }
    public function getCantidad(): int   { return $this->cantidad; }
    public function getSubtotal(): float { return $this->subtotal; }
}
