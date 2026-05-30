package com.alethanabooks.persistence;

import com.alethanabooks.modelo.Libro;
import com.alethanabooks.modelo.LibroDigital;
import com.alethanabooks.modelo.LibroFisico;
import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroRepository {

    private final Gson gson;
    private final Path ruta = Path.of(RutasDatos.LIBROS);

    public LibroRepository() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<Libro> obtenerTodos() {
        try {
            if (!Files.exists(ruta)) return new ArrayList<>();
            String json = Files.readString(ruta);
            if (json.isBlank()) return new ArrayList<>();

            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            List<Libro> libros = new ArrayList<>();

            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();
                Libro libro;
                if (obj.has("rutaArchivo")) {
                    libro = gson.fromJson(obj, LibroDigital.class);
                } else if (obj.has("ubicacionBodega")) {
                    libro = gson.fromJson(obj, LibroFisico.class);
                } else {
                    libro = gson.fromJson(obj, Libro.class);
                }
                libros.add(libro);
            }
            return libros;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo de libros.", e);
        }
    }

    public void guardarTodos(List<Libro> libros) {
        try {
            if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
            JsonArray array = new JsonArray();
            for (Libro l : libros) {
                array.add(JsonParser.parseString(gson.toJson(l)));
            }
            Files.writeString(ruta, new GsonBuilder().setPrettyPrinting().create().toJson(array));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de libros.", e);
        }
    }

    public Optional<Libro> buscarPorId(String id) {
        return obtenerTodos().stream().filter(l -> l.getId().equals(id)).findFirst();
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
        libros.removeIf(l -> l.getId().equals(id));
        guardarTodos(libros);
    }
}