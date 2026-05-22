package com.alethanabooks.persistence;

import com.alethanabooks.modelo.Libro;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class LibroRepository {
    private final JsonManager jsonManager = JsonManager.getInstancia();
    private final Path ruta = Path.of("src", "main", "resources", "data", "libros.json");

    public List<Libro> listarTodos() {
        return jsonManager.leerLista(ruta, Libro.class);
    }

    public void guardarTodos(List<Libro> libros) {
        jsonManager.guardarLista(ruta, libros);
    }

    public Optional<Libro> buscarPorId(String id) {
        return listarTodos().stream()
                .filter(libro -> libro.getId().equals(id))
                .findFirst();
    }

    public void agregar(Libro libro) {
        List<Libro> libros = listarTodos();
        libros.add(libro);
        guardarTodos(libros);
    }

    public void eliminar(String id) {
        List<Libro> libros = listarTodos();
        libros.removeIf(libro -> libro.getId().equals(id));
        guardarTodos(libros);
    }
}