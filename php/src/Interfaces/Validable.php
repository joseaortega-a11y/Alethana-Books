<?php
namespace Alethana\Interfaces;

interface Validable
{
    public function esValido(): bool;
    public function obtenerMensajeError(): string;
}
