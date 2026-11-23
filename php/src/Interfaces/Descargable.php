<?php
namespace Alethana\Interfaces;

interface Descargable
{
    public function obtenerRutaDescarga(): ?string;
    public function estaDisponibleParaDescarga(): bool;
}
