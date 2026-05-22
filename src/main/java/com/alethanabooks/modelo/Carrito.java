package com.alethanabooks.modelo;

import java.util.ArrayList;
import java.util.List;

public class Carrito {
    private Usuario usuario;
    private List<ItemCarrito> items = new ArrayList<>();

    public Carrito() {
    }

    public Carrito(Usuario usuario) {
        this.usuario = usuario;
    }

    public void agregarLibro(Libro libro, int cantidad) {
        if (libro == null) throw new IllegalArgumentException("El libro no puede ser nulo.");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        if (libro.getStock() < cantidad) throw new IllegalArgumentException("Stock insuficiente.");

        items.stream()
                .filter(item -> item.getLibro().getId().equals(libro.getId()))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.aumentarCantidad(cantidad),
                        () -> items.add(new ItemCarrito(libro, cantidad))
                );
    }

    public void eliminarLibro(String idLibro) {
        items.removeIf(item -> item.getLibro().getId().equals(idLibro));
    }

    public double calcularTotal() {
        return items.stream()
                .mapToDouble(ItemCarrito::calcularSubtotal)
                .sum();
    }

    public void vaciar() {
        items.clear();
    }

    public Usuario getUsuario() { return usuario; }
    public List<ItemCarrito> getItems() { return items; }
}