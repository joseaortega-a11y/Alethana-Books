package com.alethanabooks.factory;

import com.alethanabooks.modelo.Libro;
import com.alethanabooks.modelo.LibroDigital;
import com.alethanabooks.modelo.LibroFisico;

public class LibroFactory {

    public enum TipoLibro { FISICO, DIGITAL }

    /**
     * Factory Method principal: decide qué subclase crear según el tipo.
     *
     * @param tipo              FISICO o DIGITAL
     * @param id                Identificador único del libro
     * @param titulo            Título del libro
     * @param autor             Autor del libro
     * @param categoria         Categoría del libro
     * @param precio            Precio del libro
     * @param stock             Stock disponible
     * @param imagen            Nombre del archivo de imagen de portada
     * @param formato           Formato del libro digital (PDF, EPUB, MOBI). Ignorado para libros físicos.
     * @param rutaArchivo       Ruta del archivo descargable. Ignorado para libros físicos.
     */
    public static Libro crearLibro(TipoLibro tipo, String id, String titulo,
                                   String autor, String categoria,
                                   double precio, int stock,
                                   String imagen, String formato, String rutaArchivo,
                                   String origen) {
        return switch (tipo) {
            case FISICO -> new LibroFisico(id, titulo, autor, categoria,
                    precio, stock, imagen, 0.4, "Bodega A",
                    origen != null ? origen : "Nacional");
            case DIGITAL -> new LibroDigital(id, titulo, autor, categoria,
                    precio, stock, imagen,
                    rutaArchivo, rutaArchivo != null && !rutaArchivo.isBlank(), formato);
        };
    }

    /** Sobrecarga sin origen — para compatibilidad, asume Nacional. */
    public static Libro crearLibro(TipoLibro tipo, String id, String titulo,
                                   String autor, String categoria,
                                   double precio, int stock,
                                   String imagen, String formato, String rutaArchivo) {
        return crearLibro(tipo, id, titulo, autor, categoria, precio, stock,
                imagen, formato, rutaArchivo, "Nacional");
    }

    /**
     * Sobrecarga de compatibilidad: crea un LibroFisico con valores por defecto.
     */
    public static Libro crearLibro(String isbn, String titulo, String autor,
                                   String categoria, double precio, int stock) {
        return crearLibro(TipoLibro.FISICO, isbn, titulo, autor, categoria, precio, stock, "", "", "");
    }
}