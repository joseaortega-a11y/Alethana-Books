<?php
namespace Alethana\Modelo;

class ItemCarrito
{
    public function __construct(private Libro $libro, private int $cantidad) {}

    public function calcularSubtotal(): float
    {
        return $this->libro->getPrecio() * $this->cantidad;
    }

    public function aumentarCantidad(int $cantidad): void
    {
        if ($cantidad <= 0) throw new \InvalidArgumentException('La cantidad debe ser mayor que cero.');
        $this->cantidad += $cantidad;
    }

    public function getLibro(): Libro  { return $this->libro; }
    public function getCantidad(): int { return $this->cantidad; }
}
