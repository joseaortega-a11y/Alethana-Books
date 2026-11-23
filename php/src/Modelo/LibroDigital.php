<?php
namespace Alethana\Modelo;

use Alethana\Interfaces\Descargable;
use Alethana\Interfaces\Validable;

class LibroDigital extends Libro implements Descargable, Validable
{
    public function __construct(
        ?string $id, string $titulo, string $autor, string $categoria,
        float $precio, int $stock, ?string $imagen,
        private ?string $rutaArchivo,
        private bool $disponibleDescarga,
        private ?string $formato
    ) {
        parent::__construct($id, $titulo, $autor, $categoria, $precio, $stock, $imagen);
    }

    public function obtenerRutaDescarga(): ?string { return $this->rutaArchivo; }

    public function estaDisponibleParaDescarga(): bool
    {
        return $this->disponibleDescarga && $this->rutaArchivo !== null && trim($this->rutaArchivo) !== '';
    }

    public function esValido(): bool
    {
        return $this->obtenerMensajeError() === '';
    }

    public function obtenerMensajeError(): string
    {
        if (trim($this->titulo) === '') return 'El título no puede estar vacío.';
        if (trim($this->autor) === '')  return 'El autor no puede estar vacío.';
        if ($this->precio <= 0)         return 'El precio debe ser mayor a cero.';
        if ($this->formato === null || trim($this->formato) === '') return 'El formato es obligatorio (PDF, EPUB, MOBI).';
        return '';
    }

    public function getRutaArchivo(): ?string { return $this->rutaArchivo; }
    public function getFormato(): ?string     { return $this->formato; }
    public function setRutaArchivo(?string $ruta): void  { $this->rutaArchivo = $ruta; }
    public function setDisponibleDescarga(bool $d): void { $this->disponibleDescarga = $d; }
    public function setFormato(?string $formato): void   { $this->formato = $formato; }
}
