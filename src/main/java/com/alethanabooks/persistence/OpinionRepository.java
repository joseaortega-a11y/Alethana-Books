package com.alethanabooks.persistence;

import com.alethanabooks.modelo.Opinion;

import java.nio.file.Path;
import java.util.List;

public class OpinionRepository {

    private final JsonManager jsonManager = JsonManager.getInstancia();
    private final Path ruta = Path.of(RutasDatos.OPINIONES);

    public List<Opinion> obtenerTodas() {
        return jsonManager.leerLista(ruta, Opinion.class);
    }

    public void agregar(Opinion opinion) {
        List<Opinion> lista = obtenerTodas();
        lista.add(opinion);
        jsonManager.guardarLista(ruta, lista);
    }
}