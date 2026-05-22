package com.alethanabooks.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonManager {
    private static JsonManager instancia;
    private final Gson gson;

    private JsonManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static JsonManager getInstancia() {
        if (instancia == null) instancia = new JsonManager();
        return instancia;
    }

    public <T> List<T> leerLista(Path ruta, Class<T> clase) {
        try {
            if (!Files.exists(ruta)) return new ArrayList<>();

            String json = Files.readString(ruta);
            if (json.isBlank()) return new ArrayList<>();

            Type tipo = TypeToken.getParameterized(List.class, clase).getType();
            List<T> datos = gson.fromJson(json, tipo);

            return datos != null ? datos : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo: " + ruta, e);
        }
    }

    public <T> void guardarLista(Path ruta, List<T> datos) {
        try {
            if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
            Files.writeString(ruta, gson.toJson(datos));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo: " + ruta, e);
        }
    }
}