package com.alethanabooks.functional;

import com.alethanabooks.modelo.Libro;

@FunctionalInterface
public interface FiltroLibro {
    boolean filtrar(Libro libro);
}