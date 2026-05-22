package com.alethanabooks.modelo;

public class DetalleVenta {
    private Libro libro;
    private int cantidad;
    private double subtotal;

    public DetalleVenta() {
    }

    public DetalleVenta(Libro libro, int cantidad) {
        this.libro = libro;
        this.cantidad = cantidad;
        this.subtotal = libro.getPrecio() * cantidad;
    }

    public Libro getLibro() { return libro; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
}