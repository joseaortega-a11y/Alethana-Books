package com.alethanabooks.service;

import com.alethanabooks.modelo.Carrito;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.modelo.Usuario;

public class CarritoService {

    private Carrito carrito;

    public void iniciarCarrito(Usuario usuario) {
        carrito = new Carrito(usuario);
    }

    public void agregarLibro(Libro libro, int cantidad) {
        if (carrito == null) throw new IllegalStateException("No hay carrito activo.");
        carrito.agregarLibro(libro, cantidad);
    }

    public void eliminarLibro(String idLibro) {
        if (carrito != null) carrito.eliminarLibro(idLibro);
    }

    public void vaciarCarrito() {
        if (carrito != null) carrito.vaciar();
    }

    public Carrito getCarrito() { return carrito; }
}