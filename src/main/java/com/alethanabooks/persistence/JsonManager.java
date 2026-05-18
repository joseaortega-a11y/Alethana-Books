package com.alethanabooks.persistence;

public class JsonManager {
    private static JsonManager instancia;

    private JsonManager() {
    }

    public static JsonManager getInstancia() {
        if (instancia == null) {
            instancia = new JsonManager();
        }
        return instancia;
    }

    public void guardarDatos() {
        System.out.println("Guardando datos en JSON...");
    }

    public void cargarDatos() {
        System.out.println("Cargando datos desde JSON...");
    }
}