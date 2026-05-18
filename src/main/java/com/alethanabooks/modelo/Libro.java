package com.alethanabooks.modelo;

import com.alethanabooks.interfaces.Buscable;
import com.alethanabooks.interfaces.Descontable;
import com.alethanabooks.interfaces.Vendible;

public class Libro implements Vendible, Descontable, Buscable {
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private double precio;
    private int stock;

    public Libro(String isbn, String titulo, String autor, String categoria, double precio, int stock) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    @Override
    public double calcularPrecioFinal() {
        return precio;
    }

    @Override
    public double aplicarDescuento(double porcentaje) {
        return precio - (precio * porcentaje / 100);
    }

    @Override
    public boolean coincideCon(String texto) {
        String busqueda = texto.toLowerCase();
        return titulo.toLowerCase().contains(busqueda)
                || autor.toLowerCase().contains(busqueda)
                || isbn.toLowerCase().contains(busqueda);
    }

    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
}