<?php
namespace Alethana\Persistence;

class CodigoDescuentoRepository
{
    /** Devuelve el porcentaje (ej. 20) del código, o null si no existe. */
    public function buscarPorcentaje(string $codigo): ?int
    {
        $ps = ConexionBD::obtener()->prepare('SELECT porcentaje FROM codigo_descuento WHERE codigo = ?');
        $ps->execute([$codigo]);
        $valor = $ps->fetchColumn();
        return $valor === false ? null : (int) $valor;
    }
}
