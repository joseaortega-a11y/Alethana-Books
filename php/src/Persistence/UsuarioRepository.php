<?php
namespace Alethana\Persistence;

use Alethana\Modelo\DatosEnvio;
use Alethana\Modelo\Rol;
use Alethana\Modelo\Usuario;

class UsuarioRepository
{
    private const SELECT_BASE = "
        SELECT u.id_usuario, u.correo, u.apodo, u.contrasena_hash, u.rol,
               e.nombre_destinatario, e.direccion, e.ciudad, e.telefono
        FROM usuario u
        LEFT JOIN datos_envio e ON e.id_usuario = u.id_usuario";

    public function buscarPorCorreo(string $correo): ?Usuario
    {
        $ps = ConexionBD::obtener()->prepare(self::SELECT_BASE . ' WHERE u.correo = ?');
        $ps->execute([$correo]);
        $fila = $ps->fetch();
        return $fila ? $this->mapear($fila) : null;
    }

    public function buscarPorId(string $id): ?Usuario
    {
        $ps = ConexionBD::obtener()->prepare(self::SELECT_BASE . ' WHERE u.id_usuario = ?');
        $ps->execute([(int) $id]);
        $fila = $ps->fetch();
        return $fila ? $this->mapear($fila) : null;
    }

    public function existeAdmin(): bool
    {
        return (bool) ConexionBD::obtener()->query("SELECT 1 FROM usuario WHERE rol = 'ADMIN' LIMIT 1")->fetchColumn();
    }

    /** Inserta el usuario; el trigger trg_usuario_ai lo agrega a cliente/administrador. */
    public function agregar(Usuario $usuario, string $contrasenaHash): void
    {
        ConexionBD::obtener()
            ->prepare('INSERT INTO usuario (correo, apodo, contrasena_hash, rol) VALUES (?, ?, ?, ?)')
            ->execute([$usuario->getCorreo(), $usuario->getNombre(), $contrasenaHash, $usuario->getRol()->value]);
    }

    /** Guarda (o actualiza) los datos de envío del usuario. */
    public function actualizar(Usuario $usuario): void
    {
        $envio = $usuario->getDatosEnvio();
        if ($envio === null) return;
        ConexionBD::obtener()->prepare("
            INSERT INTO datos_envio (id_usuario, nombre_destinatario, direccion, ciudad, telefono)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE nombre_destinatario = VALUES(nombre_destinatario),
                                    direccion = VALUES(direccion),
                                    ciudad    = VALUES(ciudad),
                                    telefono  = VALUES(telefono)")
            ->execute([(int) $usuario->getId(), $envio->getNombre(), $envio->getDireccion(),
                       $envio->getCiudad(), $envio->getTelefono()]);
    }

    private function mapear(array $f): Usuario
    {
        $u = new Usuario((string) $f['id_usuario'], $f['apodo'], $f['correo'], $f['contrasena_hash'],
            $f['rol'] === 'ADMIN' ? Rol::ADMIN : Rol::USUARIO);
        if ($f['direccion'] !== null) {
            $u->setDatosEnvio(new DatosEnvio($f['nombre_destinatario'], $f['direccion'], $f['ciudad'], $f['telefono']));
        }
        return $u;
    }
}
