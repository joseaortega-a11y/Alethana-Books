package com.alethanabooks.persistence;

public class RutasDatos {

    private RutasDatos() {
    }

    public static final String CARPETA_DATA     = "src/main/resources/data/";
    public static final String CARPETA_IMAGENES = "src/main/resources/imagenes/";

    public static final String LIBROS    = CARPETA_DATA + "libros.json";
    public static final String USUARIOS  = CARPETA_DATA + "usuarios.json";
    public static final String VENTAS    = CARPETA_DATA + "ventas.json";
    public static final String FAVORITOS = CARPETA_DATA + "favoritos.json";
    public static final String OPINIONES = CARPETA_DATA + "opiniones.json";
}