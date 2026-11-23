<?php
require __DIR__ . '/../bootstrap.php';

use Alethana\Util\Sesion;

Sesion::cerrar();
redirigir('login.php');
