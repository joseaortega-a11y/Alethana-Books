package com.alethanabooks.factory;

import com.alethanabooks.modelo.Libro;
import com.alethanabooks.modelo.LibroDigital;
import com.alethanabooks.modelo.LibroFisico;

public class LibroFactory {

    public enum TipoLibro { FISICO, DIGITAL }

    // Factory Method: decide qué subclase crear
    public static Libro crearLibro(TipoLibro tipo, String id, String titulo,
                                   String autor, String categoria,
                                   double precio, int stock, String imagenSeleccionada, String formato, String imagen) {
        return switch (tipo) {
            case FISICO -> new LibroFisico(id, titulo, autor, categoria,
                    precio, stock, imagen, 0.4, "Bodega A");
            case DIGITAL -> new LibroDigital(id, titulo, autor, categoria,
                    precio, stock, imagen,
                    "/descargas/" + id + ".pdf", true, "PDF");
        };
    }

    // Mantener compatibilidad con código anterior
    public Libro crearLibro(String isbn, String titulo, String autor,
                            String categoria, double precio, int stock) {
        return crearLibro(TipoLibro.FISICO, isbn, titulo, autor, categoria, precio, stock, imagenSeleccionada, formato, "");
    }
}