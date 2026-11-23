<?php
namespace Alethana\Modelo;

class Carrito
{
    /** @var ItemCarrito[] */
    private array $items = [];

    public function agregarLibro(Libro $libro, int $cantidad): void
    {
        if ($cantidad <= 0) throw new \InvalidArgumentException('La cantidad debe ser mayor que cero.');

        $existente = $this->buscarItem($libro->getId());
        $totalDeseado = $cantidad + ($existente?->getCantidad() ?? 0);
        if ($libro->getStock() < $totalDeseado) throw new \InvalidArgumentException('Stock insuficiente.');

        if ($existente !== null) {
            $existente->aumentarCantidad($cantidad);
        } else {
            $this->items[] = new ItemCarrito($libro, $cantidad);
        }
    }

    /** Reconstruye el carrito desde la sesión; el stock se vuelve a validar al confirmar la compra. */
    public function agregarLibroSinValidar(Libro $libro, int $cantidad): void
    {
        if ($cantidad > 0) $this->items[] = new ItemCarrito($libro, $cantidad);
    }

    public function eliminarLibro(string $idLibro): void
    {
        $this->items = array_values(array_filter(
            $this->items,
            fn(ItemCarrito $i) => $i->getLibro()->getId() !== $idLibro
        ));
    }

    public function calcularTotal(): float
    {
        return array_sum(array_map(fn(ItemCarrito $i) => $i->calcularSubtotal(), $this->items));
    }

    public function tieneDigitales(): bool
    {
        foreach ($this->items as $item) {
            if ($item->getLibro() instanceof LibroDigital) return true;
        }
        return false;
    }

    public function vaciar(): void    { $this->items = []; }
    public function estaVacio(): bool { return $this->items === []; }
    /** @return ItemCarrito[] */
    public function getItems(): array { return $this->items; }

    private function buscarItem(?string $idLibro): ?ItemCarrito
    {
        foreach ($this->items as $item) {
            if ($item->getLibro()->getId() === $idLibro) return $item;
        }
        return null;
    }
}
