<?php
namespace Alethana\Persistence;

use Alethana\Modelo\DatosEnvio;
use Alethana\Modelo\DetalleVenta;
use Alethana\Modelo\Libro;
use Alethana\Modelo\Rol;
use Alethana\Modelo\Usuario;
use Alethana\Modelo\Venta;

class VentaRepository
{
    /** @return Venta[] de la más reciente a la más antigua */
    public function obtenerTodas(): array
    {
        return $this->consultar(null);
    }

    /** @return Venta[] de la más reciente a la más antigua */
    public function obtenerPorUsuario(string $idUsuario): array
    {
        return $this->consultar((int) $idUsuario);
    }

    /** ¿El usuario compró este libro? (para permitir la descarga de e-books) */
    public function usuarioComproLibro(string $idUsuario, string $idLibro): bool
    {
        $ps = ConexionBD::obtener()->prepare("
            SELECT 1 FROM detalle_venta d JOIN venta v ON v.id_venta = d.id_venta
            WHERE v.id_usuario = ? AND d.id_libro = ? LIMIT 1");
        $ps->execute([(int) $idUsuario, (int) $idLibro]);
        return (bool) $ps->fetchColumn();
    }

    private function consultar(?int $idUsuario): array
    {
        $con    = ConexionBD::obtener();
        $filtro = $idUsuario !== null ? ' WHERE v.id_usuario = ?' : '';
        $params = $idUsuario !== null ? [$idUsuario] : [];

        $ps = $con->prepare("SELECT v.*, u.correo, u.apodo, u.rol
                             FROM venta v JOIN usuario u ON u.id_usuario = v.id_usuario"
                             . $filtro . ' ORDER BY v.fecha DESC, v.id_venta DESC');
        $ps->execute($params);
        $ventas = [];
        foreach ($ps->fetchAll() as $f) {
            $ventas[(int) $f['id_venta']] = $this->mapearVenta($f);
        }

        $ps = $con->prepare("SELECT d.id_venta, d.cantidad, d.precio_unitario, l.id_libro, l.titulo, l.autor
                             FROM detalle_venta d
                             JOIN libro l ON l.id_libro = d.id_libro
                             JOIN venta v ON v.id_venta = d.id_venta" . $filtro);
        $ps->execute($params);
        foreach ($ps->fetchAll() as $f) {
            $venta = $ventas[(int) $f['id_venta']] ?? null;
            if ($venta === null) continue;
            $libro = new Libro((string) $f['id_libro'], $f['titulo'], $f['autor'], '',
                               (float) $f['precio_unitario'], 0, null);
            $venta->agregarDetalle(new DetalleVenta($libro, (int) $f['cantidad']));
        }
        return array_values($ventas);
    }

    /**
     * Guarda la venta y sus detalles en una transacción.
     * Los triggers de detalle_venta validan y descuentan el stock automáticamente.
     */
    public function guardar(Venta $venta): void
    {
        $con = ConexionBD::obtener();
        $con->beginTransaction();
        try {
            $envio = $venta->getDatosEnvio();
            $con->prepare("
                INSERT INTO venta (id_usuario, metodo_pago, tipo_descuento, codigo, subtotal, descuento, total,
                                   envio_nombre, envio_direccion, envio_ciudad, envio_telefono)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                ->execute([
                    (int) $venta->getUsuario()->getId(), strtoupper($venta->getMetodoPago()),
                    $venta->getTipoDescuento(), $venta->getCodigo(),
                    self::dinero($venta->getSubtotal()), self::dinero($venta->getDescuento()), self::dinero($venta->getTotal()),
                    $envio->getNombre(), $envio->getDireccion(), $envio->getCiudad(), $envio->getTelefono(),
                ]);
            $idVenta = (int) $con->lastInsertId();

            $ps = $con->prepare("INSERT INTO detalle_venta (id_venta, id_libro, cantidad, precio_unitario, subtotal_linea)
                                 VALUES (?, ?, ?, ?, ?)");
            foreach ($venta->getDetalles() as $d) {
                $ps->execute([$idVenta, (int) $d->getLibro()->getId(), $d->getCantidad(),
                              self::dinero($d->getLibro()->getPrecio()), self::dinero($d->getSubtotal())]);
            }
            $con->commit();
            $venta->setId((string) $idVenta);
        } catch (\PDOException $e) {
            $con->rollBack();
            // Los triggers lanzan mensajes como "Stock insuficiente..." que conviene mostrar
            throw new \RuntimeException($e->errorInfo[2] ?? $e->getMessage(), 0, $e);
        }
    }

    private function mapearVenta(array $f): Venta
    {
        $u = new Usuario((string) $f['id_usuario'], $f['apodo'], $f['correo'], null,
                         $f['rol'] === 'ADMIN' ? Rol::ADMIN : Rol::USUARIO);
        $envio = new DatosEnvio($f['envio_nombre'], $f['envio_direccion'], $f['envio_ciudad'], $f['envio_telefono']);

        $v = new Venta((string) $f['id_venta'], $u, [], self::metodoDesdeBD($f['metodo_pago']),
                       (float) $f['total'], 'PAGADO', $envio);
        $v->setFecha(new \DateTimeImmutable($f['fecha']));
        $v->setSubtotal((float) $f['subtotal']);
        $v->setDescuento((float) $f['descuento']);
        $v->setTipoDescuento($f['tipo_descuento']);
        $v->setCodigo($f['codigo']);
        return $v;
    }

    /** Montos DECIMAL(12,2): se envían como texto con 2 decimales exactos (evita errores de float). */
    private static function dinero(float $valor): string
    {
        return number_format($valor, 2, '.', '');
    }

    // En la BD: TARJETA / PSE / EFECTY; en la app: Tarjeta / PSE / Efecty
    private static function metodoDesdeBD(?string $metodo): string
    {
        return match ($metodo) {
            'TARJETA' => 'Tarjeta',
            'EFECTY'  => 'Efecty',
            null      => '',
            default   => $metodo,
        };
    }
}
