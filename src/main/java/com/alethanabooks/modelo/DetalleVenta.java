package com.alethanabooks.modelo;

public class DetalleVenta {
    private Libro libro;
    private int cantidad;

    public DetalleVenta(Libro libro, int cantidad) {
        this.libro = libro;
        this.cantidad = cantidad;
    }

    public double calcularSubtotal() {
        return libro.getPrecio() * cantidad;
    }

    public Libro getLibro() {
        return libro;
    }

    public int getCantidad() {
        return cantidad;
    }
}