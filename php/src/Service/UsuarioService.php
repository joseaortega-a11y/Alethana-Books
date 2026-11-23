<?php
namespace Alethana\Service;

use Alethana\Modelo\Rol;
use Alethana\Modelo\Usuario;
use Alethana\Persistence\UsuarioRepository;

class UsuarioService
{
    private UsuarioRepository $usuarioRepository;

    public function __construct()
    {
        $this->usuarioRepository = new UsuarioRepository();
    }

    public function autenticar(string $correo, string $contrasena): ?Usuario
    {
        $u = $this->usuarioRepository->buscarPorCorreo($correo);
        return ($u !== null && $u->credencialesCoinciden($correo, $contrasena)) ? $u : null;
    }

    public function esAdmin(string $correo, string $contrasena): bool
    {
        return $this->autenticar($correo, $contrasena)?->esAdmin() ?? false;
    }

    /** Registra un cliente nuevo. Si el correo ya existe no hace nada. */
    public function registrar(string $nombre, string $correo, string $contrasena): void
    {
        if ($this->usuarioRepository->buscarPorCorreo($correo) !== null) return;
        $nuevo = new Usuario(null, $nombre, $correo, null, Rol::USUARIO);
        $this->usuarioRepository->agregar($nuevo, Usuario::hashear($contrasena));
    }

    public function inicializarAdminPorDefecto(): void
    {
        if (!$this->usuarioRepository->existeAdmin()) {
            $admin = new Usuario(null, 'Admin', 'admin@alethana.com', null, Rol::ADMIN);
            $this->usuarioRepository->agregar($admin, Usuario::hashear('admin123'));
        }
    }

    public function buscarPorId(string $id): ?Usuario
    {
        return $this->usuarioRepository->buscarPorId($id);
    }

    public function guardarDatosEnvio(Usuario $usuario): void
    {
        $this->usuarioRepository->actualizar($usuario);
    }
}
