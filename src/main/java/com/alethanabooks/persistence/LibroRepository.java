package com.alethanabooks.persistence;

import com.alethanabooks.modelo.Libro;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class LibroRepository {

    private final JsonManager jsonManager = JsonManager.getInstancia();
    private final Path ruta = Path.of(RutasDatos.LIBROS);

    public List<Libro> obtenerTodos() {
        return jsonManager.leerLista(ruta, Libro.class);
    }

    public void guardarTodos(List<Libro> libros) {
        jsonManager.guardarLista(ruta, libros);
    }

    public Optional<Libro> buscarPorId(String id) {
        return obtenerTodos().stream()
                .filter(libro -> libro.getId().equals(id))
                .findFirst();
    }

    public void agregar(Libro libro) {
        List<Libro> libros = obtenerTodos();
        libros.add(libro);
        guardarTodos(libros);
    }

    public void actualizar(Libro libroActualizado) {
        List<Libro> libros = obtenerTodos();
        libros.replaceAll(l -> l.getId().equals(libroActualizado.getId()) ? libroActualizado : l);
        guardarTodos(libros);
    }

    public void eliminar(String id) {
        List<Libro> libros = obtenerTodos();
        libros.removeIf(libro -> libro.getId().equals(id));
        guardarTodos(libros);
    }
}