<?php
namespace Alethana\Modelo;

class Libro
{
    public function __construct(
        protected ?string $id,
        protected string $titulo,
        protected string $autor,
        protected string $categoria,
        protected float $precio,
        protected int $stock,
        protected ?string $imagen
    ) {}

    public function coincideCon(?string $texto): bool
    {
        if ($texto === null || trim($texto) === '') {
            return true;
        }
        $busqueda = mb_strtolower(trim($texto));
        return str_contains(mb_strtolower($this->titulo), $busqueda)
            || str_contains(mb_strtolower($this->autor), $busqueda)
            || str_contains(mb_strtolower($this->categoria), $busqueda);
    }

    public function calcularPrecioFinal(): float { return $this->precio; }
    public function hayStock(): bool             { return $this->stock > 0; }

    public function reducirStock(int $cantidad): void
    {
        if ($cantidad <= 0) throw new \InvalidArgumentException('La cantidad debe ser mayor que cero.');
        if ($cantidad > $this->stock) throw new \InvalidArgumentException('No hay stock suficiente.');
        $this->stock -= $cantidad;
    }

    public function getId(): ?string       { return $this->id; }
    public function getTitulo(): string    { return $this->titulo; }
    public function getAutor(): string     { return $this->autor; }
    public function getCategoria(): string { return $this->categoria; }
    public function getPrecio(): float     { return $this->precio; }
    public function getStock(): int        { return $this->stock; }
    public function getImagen(): ?string   { return $this->imagen; }

    public function setId(?string $id): void              { $this->id = $id; }
    public function setTitulo(string $titulo): void       { $this->titulo = $titulo; }
    public function setAutor(string $autor): void         { $this->autor = $autor; }
    public function setCategoria(string $categoria): void { $this->categoria = $categoria; }
    public function setPrecio(float $precio): void        { $this->precio = $precio; }
    public function setStock(int $stock): void            { $this->stock = $stock; }
    public function setImagen(?string $imagen): void      { $this->imagen = $imagen; }
}
