package com.alethanabooks.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abre conexiones a la base de datos MySQL/MariaDB de XAMPP.
 * Cada repositorio abre una conexión por operación (try-with-resources).
 */
public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/alethana_books?useUnicode=true&characterEncoding=UTF-8";
    private static final String USUARIO = "root";
    private static final String CLAVE   = "";   // XAMPP por defecto no tiene contraseña

    private ConexionBD() {}

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
