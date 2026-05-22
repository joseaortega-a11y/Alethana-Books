package com.alethanabooks.modelo;

import com.alethanabooks.interfaces.Descargable;

public class LibroDigital extends Libro implements Descargable {

    private String rutaArchivo;
    private boolean disponibleDescarga;

    public LibroDigital() {
    }

    public LibroDigital(String id, String titulo, String autor, String categoria,
                        double precio, int stock, String imagen,
                        String rutaArchivo, boolean disponibleDescarga) {
        super(id, titulo, autor, categoria, precio, stock, imagen);
        this.rutaArchivo = rutaArchivo;
        this.disponibleDescarga = disponibleDescarga;
    }

    @Override
    public String obtenerRutaDescarga() {
        return rutaArchivo;
    }

    @Override
    public boolean estaDisponibleParaDescarga() {
        return disponibleDescarga && rutaArchivo != null && !rutaArchivo.isBlank();
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public boolean isDisponibleDescarga() {
        return disponibleDescarga;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public void setDisponibleDescarga(boolean disponibleDescarga) {
        this.disponibleDescarga = disponibleDescarga;
    }
}