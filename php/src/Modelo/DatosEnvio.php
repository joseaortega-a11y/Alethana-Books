<?php
namespace Alethana\Modelo;

class DatosEnvio
{
    public function __construct(
        private ?string $nombre = null,
        private ?string $direccion = null,
        private ?string $ciudad = null,
        private ?string $telefono = null
    ) {}

    public function estaCompleto(): bool
    {
        foreach ([$this->nombre, $this->direccion, $this->ciudad, $this->telefono] as $campo) {
            if ($campo === null || trim($campo) === '') return false;
        }
        return true;
    }

    public function getNombre(): ?string    { return $this->nombre; }
    public function getDireccion(): ?string { return $this->direccion; }
    public function getCiudad(): ?string    { return $this->ciudad; }
    public function getTelefono(): ?string  { return $this->telefono; }
}
