package com.alethanabooks.modelo;

public class LibroFisico extends Libro {

    private double peso;
    private String ubicacionBodega;
    private String origen; // "Nacional" o "Importado"

    public LibroFisico() {}

    public LibroFisico(String id, String titulo, String autor, String categoria,
                       double precio, int stock, String imagen,
                       double peso, String ubicacionBodega, String origen) {
        super(id, titulo, autor, categoria, precio, stock, imagen);
        this.peso            = peso;
        this.ubicacionBodega = ubicacionBodega;
        this.origen          = origen != null ? origen : "Nacional";
    }

    public double getPeso()              { return peso; }
    public String getUbicacionBodega()   { return ubicacionBodega; }
    public String getOrigen()            { return origen != null ? origen : "Nacional"; }

    public void setPeso(double peso)                  { this.peso = peso; }
    public void setUbicacionBodega(String ub)         { this.ubicacionBodega = ub; }
    public void setOrigen(String origen)              { this.origen = origen; }
}