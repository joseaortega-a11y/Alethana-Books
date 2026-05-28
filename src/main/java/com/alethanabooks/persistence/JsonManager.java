package com.alethanabooks.persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonManager {
    private static JsonManager instancia;
    private final Gson gson;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private JsonManager() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                // TypeAdapter para LocalDateTime
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, type, ctx) -> new JsonPrimitive(src.format(FORMATO)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, type, ctx) -> LocalDateTime.parse(json.getAsString(), FORMATO))
                // TypeAdapter para LocalDate (por si acaso)
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
                        (src, type, ctx) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                        (json, type, ctx) -> LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
                .create();

        inicializarArchivos();
    }

    public static JsonManager getInstancia() {
        if (instancia == null) instancia = new JsonManager();
        return instancia;
    }

    /**
     * Al arrancar, crea las carpetas necesarias y copia los JSON semilla
     * del classpath al directorio del usuario si todavía no existen.
     */
    private void inicializarArchivos() {
        try {
            Files.createDirectories(Path.of(RutasDatos.CARPETA_DATA));
            Files.createDirectories(Path.of(RutasDatos.CARPETA_IMAGENES));
            Files.createDirectories(Path.of(RutasDatos.CARPETA_DESCARGABLES));

            copiarSiNoExiste("/data/libros.json",  RutasDatos.LIBROS);
            copiarSiNoExiste("/data/usuarios.json", RutasDatos.USUARIOS);
            crearSiNoExiste(RutasDatos.VENTAS);
            crearSiNoExiste(RutasDatos.OPINIONES);
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron inicializar los archivos de datos.", e);
        }
    }

    private void copiarSiNoExiste(String recurso, String destino) throws IOException {
        Path dest = Path.of(destino);
        if (!Files.exists(dest)) {
            InputStream stream = getClass().getResourceAsStream(recurso);
            if (stream != null) {
                Files.copy(stream, dest, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.writeString(dest, "[]");
            }
        }
    }

    private void crearSiNoExiste(String ruta) throws IOException {
        Path path = Path.of(ruta);
        if (!Files.exists(path)) {
            Files.writeString(path, "[]");
        }
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