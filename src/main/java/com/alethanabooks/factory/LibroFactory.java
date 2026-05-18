package com.alethanabooks.factory;

import com.alethanabooks.modelo.Libro;

public class LibroFactory {

    public Libro crearLibro(String isbn, String titulo, String autor, String categoria, double precio, int stock) {
        return new Libro(isbn, titulo, autor, categoria, precio, stock);
    }
}