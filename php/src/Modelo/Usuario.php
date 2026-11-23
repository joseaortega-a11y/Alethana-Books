<?php
namespace Alethana\Modelo;

class Usuario
{
    private ?DatosEnvio $datosEnvio = null;

    public function __construct(
        private ?string $id,
        private string $nombre,
        private string $correo,
        private ?string $contrasena,   // hash SHA-256 guardado en la BD
        private Rol $rol
    ) {}

    /** Acepta el hash SHA-256 o, por compatibilidad con datos de prueba, el texto plano. */
    public function credencialesCoinciden(string $correo, string $contrasena): bool
    {
        if ($this->contrasena === null) return false;
        return strcasecmp($this->correo, $correo) === 0
            && (hash_equals(strtolower($this->contrasena), self::hashear($contrasena))
                || hash_equals($this->contrasena, $contrasena));
    }

    public static function hashear(string $texto): string
    {
        return hash('sha256', $texto);
    }

    public function getId(): ?string             { return $this->id; }
    public function getNombre(): string          { return $this->nombre; }
    public function getCorreo(): string          { return $this->correo; }
    public function getRol(): Rol                { return $this->rol; }
    public function esAdmin(): bool              { return $this->rol === Rol::ADMIN; }
    public function getDatosEnvio(): ?DatosEnvio { return $this->datosEnvio; }
    public function setDatosEnvio(?DatosEnvio $d): void { $this->datosEnvio = $d; }
}
