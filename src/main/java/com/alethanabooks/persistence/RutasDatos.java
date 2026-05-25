package com.alethanabooks.persistence;

import java.nio.file.Path;

public class RutasDatos {
    private RutasDatos() {}

    private static final String BASE = Path.of("").toAbsolutePath().toString();

    public static final String CARPETA_DATA     = BASE + "/src/main/resources/data/";
    public static final String CARPETA_IMAGENES = BASE + "/src/main/resources/imagenes/";
    public static final String LIBROS    = CARPETA_DATA + "libros.json";
    public static final String USUARIOS  = CARPETA_DATA + "usuarios.json";
    public static final String VENTAS    = CARPETA_DATA + "ventas.json";
    public static final String FAVORITOS = CARPETA_DATA + "favoritos.json";
    public static final String OPINIONES = CARPETA_DATA + "opiniones.json";
}