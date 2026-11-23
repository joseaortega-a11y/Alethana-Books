<?php
namespace Alethana\Util;

use Alethana\Modelo\Usuario;
use Alethana\Service\UsuarioService;

/** Equivalente web de SesionActual: el usuario autenticado se guarda en $_SESSION. */
final class Sesion
{
    private static ?Usuario $usuario = null;

    public static function iniciar(Usuario $u): void
    {
        session_regenerate_id(true);
        $_SESSION['id_usuario'] = $u->getId();
        $_SESSION['carrito']    = [];
        self::$usuario = $u;
    }

    public static function cerrar(): void
    {
        $_SESSION = [];
        session_regenerate_id(true);
        self::$usuario = null;
    }

    public static function haySesion(): bool
    {
        return self::getUsuario() !== null;
    }

    /** Recarga el usuario desde la BD (así los datos de envío siempre están actualizados). */
    public static function getUsuario(): ?Usuario
    {
        if (self::$usuario === null && isset($_SESSION['id_usuario'])) {
            self::$usuario = (new UsuarioService())->buscarPorId((string) $_SESSION['id_usuario']);
        }
        return self::$usuario;
    }

    public static function requerirUsuario(): Usuario
    {
        $u = self::getUsuario();
        if ($u === null) {
            flash('info', 'Inicia sesión para continuar.');
            redirigir('login.php');
        }
        return $u;
    }

    public static function requerirAdmin(): Usuario
    {
        $u = self::getUsuario();
        if ($u === null || !$u->esAdmin()) {
            flash('error', 'Acceso solo para administradores.');
            redirigir('login.php');
        }
        return $u;
    }
}
