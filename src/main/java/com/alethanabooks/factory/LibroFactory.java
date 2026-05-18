package com.alethanabooks.factory;

import com.alethanabooks.modelo.Libro;

public class LibroFactory {
    public static Libro crearLibroImportado(String isbn, String titulo, String autor, double precio, int stock) {
        return new Libro(isbn, titulo, autor, "Importado", precio, stock);
    }

    public static Libro crearLibroNacional(String isbn, String titulo, String autor, double precio, int stock) {
        return new Libro(isbn, titulo, autor, "Nacional", precio, stock);
    }
}