<?php
namespace Alethana\Persistence;

use Alethana\Modelo\Opinion;

class OpinionRepository
{
    /** @return Opinion[] de la más reciente a la más antigua */
    public function obtenerTodas(): array
    {
        $rs = ConexionBD::obtener()->query("
            SELECT o.id_opinion, o.id_usuario, u.apodo, o.texto, o.calificacion, o.fecha_hora
            FROM opinion o JOIN usuario u ON u.id_usuario = o.id_usuario
            ORDER BY o.fecha_hora DESC, o.id_opinion DESC");

        return array_map(fn(array $f) => new Opinion(
            (string) $f['id_opinion'],
            (string) $f['id_usuario'],
            $f['apodo'],
            $f['texto'],
            (int) $f['calificacion'],
            new \DateTimeImmutable($f['fecha_hora'])
        ), $rs->fetchAll());
    }

    public function agregar(Opinion $opinion): void
    {
        ConexionBD::obtener()
            ->prepare('INSERT INTO opinion (id_usuario, texto, calificacion) VALUES (?, ?, ?)')
            ->execute([(int) $opinion->getIdUsuario(), $opinion->getTexto(), $opinion->getEstrellas()]);
    }
}
