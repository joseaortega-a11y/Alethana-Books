package com.alethanabooks.persistence;

/** Carpetas locales para portadas y archivos descargables (los datos viven en MySQL). */
public class RutasDatos {

    private RutasDatos() {}

    public static final String CARPETA_BASE         = System.getProperty("user.home") + "/AlethanaBooks/";
    public static final String CARPETA_IMAGENES     = CARPETA_BASE + "imagenes/";
    public static final String CARPETA_DESCARGABLES = CARPETA_BASE + "descargables/";
}
