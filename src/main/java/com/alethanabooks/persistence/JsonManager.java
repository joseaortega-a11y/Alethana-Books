package com.alethanabooks.persistence;

import com.alethanabooks.modelo.Cliente;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.modelo.Venta;

import java.util.List;

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

    public void guardarLibros(List<Libro> libros) {
    }

    public void guardarClientes(List<Cliente> clientes) {
    }

    public void guardarVentas(List<Venta> ventas) {
    }
}