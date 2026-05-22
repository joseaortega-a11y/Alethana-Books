package com.alethanabooks.modelo;

public class Libro {

    private String id;
    private String titulo;
    private String autor;
    private String categoria;
    private double precio;
    private int stock;
    private String imagen;

    public Libro() {
    }

    public Libro(String id, String titulo, String autor, String categoria, double precio, int stock, String imagen) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.imagen = imagen;
    }

    public boolean coincideCon(String texto) {
        if (texto == null || texto.isBlank()) {
            return true;
        }

        String busqueda = texto.toLowerCase();

        return titulo.toLowerCase().contains(busqueda)
                || autor.toLowerCase().contains(busqueda)
                || categoria.toLowerCase().contains(busqueda);
    }

    public double calcularPrecioFinal() {
        return precio;
    }

    public boolean hayStock() {
        return stock > 0;
    }

    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        if (cantidad > stock) {
            throw new IllegalArgumentException("No hay stock suficiente.");
        }

        stock -= cantidad;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}