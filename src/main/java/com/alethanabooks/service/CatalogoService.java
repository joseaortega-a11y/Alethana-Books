package com.alethanabooks.service;

import com.alethanabooks.functional.FiltroLibro;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.persistence.LibroRepository;

import java.util.*;
import java.util.stream.Collectors;

public class CatalogoService {

    private final LibroRepository libroRepository = new LibroRepository();

    public List<Libro> obtenerTodos() {
        return libroRepository.obtenerTodos();
    }

    public Optional<Libro> buscarPorIdOpt(String id) {
        return obtenerTodos().stream()
                .filter(l -> l.getId().equals(id))
                .findFirst();
    }

    public Libro buscarPorId(String id) {
        return buscarPorIdOpt(id).orElse(null);
    }

    public List<Libro> buscar(String texto) {
        FiltroLibro filtro = libro -> libro.coincideCon(texto);
        return obtenerTodos().stream()
                .filter(filtro::filtrar)
                .collect(Collectors.toList());
    }

    public List<Libro> filtrarPorCategoria(String categoria) {
        FiltroLibro filtro = libro -> libro.getCategoria().equalsIgnoreCase(categoria);
        return obtenerTodos().stream()
                .filter(filtro::filtrar)
                .collect(Collectors.toList());
    }

    public List<Libro> obtenerRecomendadosAleatorios(int limite) {
        List<Libro> libros = new ArrayList<>(obtenerTodos());
        Collections.shuffle(libros);
        return libros.stream().limit(limite).collect(Collectors.toList());
    }

    // Últimos N añadidos (los últimos de la lista JSON)
    public List<Libro> obtenerUltimos(int limite) {
        List<Libro> todos = obtenerTodos();
        int desde = Math.max(0, todos.size() - limite);
        List<Libro> ultimos = new ArrayList<>(todos.subList(desde, todos.size()));
        Collections.reverse(ultimos); // más reciente primero
        return ultimos;
    }

    public void agregar(Libro libro)      { libroRepository.agregar(libro); }
    public void eliminar(String id)       { libroRepository.eliminar(id); }
    public void actualizar(Libro libro)   { libroRepository.actualizar(libro); }
}