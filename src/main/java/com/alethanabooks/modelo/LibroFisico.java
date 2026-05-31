package com.alethanabooks.modelo;

public class LibroFisico extends Libro {

    private double peso;
    private String ubicacionBodega;
    private String origen;

    public LibroFisico() {}

    public LibroFisico(String id, String titulo, String autor, String categoria,
                       double precio, int stock, String imagen,
                       double peso, String ubicacionBodega, String origen) {
        super(id, titulo, autor, categoria, precio, stock, imagen);
        this.peso            = peso;
        this.ubicacionBodega = ubicacionBodega;
        this.origen          = origen != null ? origen : "Nacional";
    }

    public String getOrigen()            { return origen != null ? origen : "Nacional"; }


    public void setOrigen(String origen)              { this.origen = origen; }
}