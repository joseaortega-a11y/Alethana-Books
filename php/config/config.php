<?php
/**
 * Configuración de Alethana Books (versión PHP).
 * Valores por defecto pensados para XAMPP: MySQL/MariaDB en localhost, usuario root sin contraseña.
 */
return [
    'db' => [
        'host'     => getenv('ALETHANA_DB_HOST') ?: 'localhost',
        'puerto'   => getenv('ALETHANA_DB_PORT') ?: '3306',
        'nombre'   => getenv('ALETHANA_DB_NAME') ?: 'alethana_books',
        'usuario'  => getenv('ALETHANA_DB_USER') ?: 'root',
        'clave'    => getenv('ALETHANA_DB_PASS') ?: '',
    ],
    // Carpeta (fuera de public/) donde se guardan los archivos de los libros digitales
    'carpeta_descargables' => __DIR__ . '/../descargables/',
    // Carpeta pública de portadas
    'carpeta_imagenes'     => __DIR__ . '/../public/imagenes/',
];
