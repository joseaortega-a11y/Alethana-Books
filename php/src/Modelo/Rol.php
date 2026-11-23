<?php
namespace Alethana\Modelo;

/** El valor es el que se guarda en la columna usuario.rol */
enum Rol: string
{
    case ADMIN   = 'ADMIN';
    case USUARIO = 'USER';
}
