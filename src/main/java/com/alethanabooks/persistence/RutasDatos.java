package com.alethanabooks.persistence;

public class RutasDatos {

    private RutasDatos() {}

    public static final String CARPETA_BASE         = System.getProperty("user.home") + "/AlethanaBooks/";
    public static final String CARPETA_DATA         = CARPETA_BASE + "data/";
    public static final String CARPETA_IMAGENES     = CARPETA_BASE + "imagenes/";
    public static final String CARPETA_DESCARGABLES = CARPETA_BASE + "descargables/";

    public static final String LIBROS    = CARPETA_DATA + "libros.json";
    public static final String USUARIOS  = CARPETA_DATA + "usuarios.json";
    public static final String VENTAS    = CARPETA_DATA + "ventas.json";
    public static final String OPINIONES = CARPETA_DATA + "opiniones.json";
}