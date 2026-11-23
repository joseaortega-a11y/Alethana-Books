<?php
namespace Alethana\Persistence;

use Alethana\Modelo\Libro;
use Alethana\Modelo\LibroDigital;
use Alethana\Modelo\LibroFisico;

class LibroRepository
{
    private const SELECT_BASE = "
        SELECT l.id_libro, l.titulo, l.autor, c.nombre AS categoria, l.precio, l.stock,
               l.ruta_imagen, l.tipo,
               f.peso, f.bodega, f.origen,
               d.formato, d.ruta_archivo
        FROM libro l
        JOIN categoria c ON c.id_categoria = l.id_categoria
        LEFT JOIN libro_fisico  f ON f.id_libro = l.id_libro
        LEFT JOIN libro_digital d ON d.id_libro = l.id_libro
        WHERE l.activo = 1";

    /** @return Libro[] */
    public function obtenerTodos(): array
    {
        $rs = ConexionBD::obtener()->query(self::SELECT_BASE . ' ORDER BY l.fecha_registro, l.id_libro');
        return array_map([$this, 'mapear'], $rs->fetchAll());
    }

    public function buscarPorId(string $id): ?Libro
    {
        if (!ctype_digit($id)) return null;
        $ps = ConexionBD::obtener()->prepare(self::SELECT_BASE . ' AND l.id_libro = ?');
        $ps->execute([(int) $id]);
        $fila = $ps->fetch();
        return $fila ? $this->mapear($fila) : null;
    }

    /** @return string[] */
    public function obtenerCategorias(): array
    {
        return ConexionBD::obtener()->query('SELECT nombre FROM categoria ORDER BY nombre')
            ->fetchAll(\PDO::FETCH_COLUMN);
    }

    public function agregar(Libro $libro): void
    {
        $con = ConexionBD::obtener();
        $con->beginTransaction();
        try {
            $ps = $con->prepare("
                INSERT INTO libro (titulo, autor, id_categoria, precio, stock, ruta_imagen, tipo)
                VALUES (?, ?, (SELECT id_categoria FROM categoria WHERE nombre = ?), ?, ?, ?, ?)");
            $ps->execute([
                $libro->getTitulo(), $libro->getAutor(), $libro->getCategoria(),
                $libro->getPrecio(), $libro->getStock(), self::vacioANull($libro->getImagen()),
                $libro instanceof LibroDigital ? 'DIGITAL' : 'FISICO',
            ]);
            $idGenerado = (int) $con->lastInsertId();

            if ($libro instanceof LibroDigital) {
                $con->prepare('INSERT INTO libro_digital (id_libro, formato, ruta_archivo) VALUES (?, ?, ?)')
                    ->execute([$idGenerado, $libro->getFormato(), $libro->getRutaArchivo() ?? '']);
            } elseif ($libro instanceof LibroFisico) {
                $con->prepare('INSERT INTO libro_fisico (id_libro, peso, bodega, origen) VALUES (?, ?, ?, ?)')
                    ->execute([$idGenerado, $libro->getPeso(), $libro->getUbicacionBodega() ?? 'Bodega Central',
                               self::origenABD($libro->getOrigen())]);
            }
            $con->commit();
            $libro->setId((string) $idGenerado);
        } catch (\Throwable $e) {
            $con->rollBack();
            throw new \RuntimeException('No se pudo agregar el libro: ' . self::mensaje($e), 0, $e);
        }
    }

    public function actualizar(Libro $libro): void
    {
        $con = ConexionBD::obtener();
        $id  = (int) $libro->getId();
        $con->beginTransaction();
        try {
            $con->prepare("
                UPDATE libro
                   SET titulo = ?, autor = ?,
                       id_categoria = (SELECT id_categoria FROM categoria WHERE nombre = ?),
                       precio = ?, stock = ?, ruta_imagen = ?
                 WHERE id_libro = ?")
                ->execute([
                    $libro->getTitulo(), $libro->getAutor(), $libro->getCategoria(),
                    $libro->getPrecio(), $libro->getStock(), self::vacioANull($libro->getImagen()), $id,
                ]);

            if ($libro instanceof LibroDigital) {
                $con->prepare('UPDATE libro_digital SET formato = ?, ruta_archivo = ? WHERE id_libro = ?')
                    ->execute([$libro->getFormato(), $libro->getRutaArchivo() ?? '', $id]);
            } elseif ($libro instanceof LibroFisico) {
                $con->prepare('UPDATE libro_fisico SET origen = ? WHERE id_libro = ?')
                    ->execute([self::origenABD($libro->getOrigen()), $id]);
            }
            $con->commit();
        } catch (\Throwable $e) {
            $con->rollBack();
            throw new \RuntimeException('No se pudo actualizar el libro: ' . self::mensaje($e), 0, $e);
        }
    }

    /** Borrado lógico: el libro puede tener ventas asociadas, así que solo se desactiva. */
    public function eliminar(string $id): void
    {
        ConexionBD::obtener()->prepare('UPDATE libro SET activo = 0 WHERE id_libro = ?')->execute([(int) $id]);
    }

    private function mapear(array $f): Libro
    {
        $id = (string) $f['id_libro'];
        if ($f['tipo'] === 'DIGITAL') {
            $ruta = $f['ruta_archivo'];
            return new LibroDigital($id, $f['titulo'], $f['autor'], $f['categoria'], (float) $f['precio'],
                (int) $f['stock'], $f['ruta_imagen'], $ruta, $ruta !== null && trim($ruta) !== '', $f['formato']);
        }
        return new LibroFisico($id, $f['titulo'], $f['autor'], $f['categoria'], (float) $f['precio'],
            (int) $f['stock'], $f['ruta_imagen'], (float) $f['peso'], $f['bodega'], self::origenDesdeBD($f['origen']));
    }

    // En la BD se guarda NACIONAL / IMPORTADO; la app muestra Nacional / Importado
    private static function origenABD(?string $origen): string
    {
        return strcasecmp($origen ?? '', 'Importado') === 0 ? 'IMPORTADO' : 'NACIONAL';
    }

    private static function origenDesdeBD(?string $origen): string
    {
        return strcasecmp($origen ?? '', 'IMPORTADO') === 0 ? 'Importado' : 'Nacional';
    }

    private static function vacioANull(?string $s): ?string
    {
        return ($s === null || trim($s) === '') ? null : $s;
    }

    private static function mensaje(\Throwable $e): string
    {
        return ($e instanceof \PDOException && isset($e->errorInfo[2])) ? $e->errorInfo[2] : $e->getMessage();
    }
}
