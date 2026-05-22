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

    public double getPeso() {
        return peso;
    }

    public String getUbicacionBodega() {
        return ubicacionBodega;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public void setUbicacionBodega(String ubicacionBodega) {
        this.ubicacionBodega = ubicacionBodega;
    }
}