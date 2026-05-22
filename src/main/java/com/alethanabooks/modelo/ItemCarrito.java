package com.alethanabooks.modelo;

public class ItemCarrito {
    private Libro libro;
    private int cantidad;

    public ItemCarrito() {
    }

    public ItemCarrito(Libro libro, int cantidad) {
        this.libro = libro;
        this.cantidad = cantidad;
    }

    public double calcularSubtotal() {
        return libro.getPrecio() * cantidad;
    }

    public void aumentarCantidad(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        this.cantidad += cantidad;
    }

    public Libro getLibro() { return libro; }
    public int getCantidad() { return cantidad; }
}