package com.alethanabooks.modelo;

public class LibroFisico extends Libro {

    private double peso;
    private String ubicacionBodega;

    public LibroFisico() {
    }

    public LibroFisico(String id, String titulo, String autor, String categoria,
                       double precio, int stock, String imagen,
                       double peso, String ubicacionBodega) {
        super(id, titulo, autor, categoria, precio, stock, imagen);
        this.peso = peso;
        this.ubicacionBodega = ubicacionBodega;
    }
}