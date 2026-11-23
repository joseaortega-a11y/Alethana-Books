<?php
namespace Alethana\Modelo;

class LibroFisico extends Libro
{
    private string $origen;

    public function __construct(
        ?string $id, string $titulo, string $autor, string $categoria,
        float $precio, int $stock, ?string $imagen,
        private float $peso,
        private ?string $ubicacionBodega,
        ?string $origen
    ) {
        parent::__construct($id, $titulo, $autor, $categoria, $precio, $stock, $imagen);
        $this->origen = $origen ?? 'Nacional';
    }

    public function getOrigen(): string           { return $this->origen; }
    public function getPeso(): float              { return $this->peso > 0 ? $this->peso : 0.4; }
    public function getUbicacionBodega(): ?string { return $this->ubicacionBodega; }
    public function esImportado(): bool           { return $this->origen === 'Importado'; }

    public function setOrigen(string $origen): void { $this->origen = $origen; }
}
