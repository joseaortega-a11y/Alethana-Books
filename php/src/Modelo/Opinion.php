<?php
namespace Alethana\Modelo;

class Opinion
{
    private int $estrellas;
    private \DateTimeImmutable $fecha;

    public function __construct(
        private ?string $id,
        private string $idUsuario,
        private string $nombreUsuario,
        private string $texto,
        int $estrellas,
        ?\DateTimeImmutable $fecha = null
    ) {
        $this->estrellas = max(1, min(5, $estrellas));
        $this->fecha     = $fecha ?? new \DateTimeImmutable();
    }

    public function getId(): ?string               { return $this->id; }
    public function getIdUsuario(): string         { return $this->idUsuario; }
    public function getNombreUsuario(): string     { return $this->nombreUsuario; }
    public function getTexto(): string             { return $this->texto; }
    public function getEstrellas(): int            { return $this->estrellas; }
    public function getFecha(): \DateTimeImmutable { return $this->fecha; }
}
