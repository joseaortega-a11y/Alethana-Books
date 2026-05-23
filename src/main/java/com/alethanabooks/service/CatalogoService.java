package com.alethanabooks.service;

import com.alethanabooks.functional.FiltroLibro;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.persistence.LibroRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogoService {

    private final LibroRepository libroRepository;

    public CatalogoService() {
        this.libroRepository = new LibroRepository();
    }

    public List<Libro> obtenerTodos() {
        return libroRepository.obtenerTodos();
    }

    public List<Libro> buscar(String texto) {
        FiltroLibro filtro = libro -> libro.coincideCon(texto);
        return libroRepository.obtenerTodos().stream()
                .filter(filtro::filtrar).collect(Collectors.toList());
    }

    public List<Libro> filtrarPorCategoria(String categoria) {
        FiltroLibro filtro = libro -> libro.getCategoria().equalsIgnoreCase(categoria);
        return libroRepository.obtenerTodos().stream()
                .filter(filtro::filtrar).collect(Collectors.toList());
    }

    public List<Libro> obtenerRecomendadosAleatorios(int limite) {
        List<Libro> libros = new ArrayList<>(libroRepository.obtenerTodos());
        Collections.shuffle(libros);
        return libros.stream().limit(limite).collect(Collectors.toList());
    }

    public void agregar(Libro libro) {
        libroRepository.agregar(libro);
    }

    public void eliminar(String id) {
        libroRepository.eliminar(id);
    }

    public void actualizar(Libro libro) {
        libroRepository.actualizar(libro);
    }
}
