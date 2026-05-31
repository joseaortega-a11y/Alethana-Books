package com.alethanabooks.modelo;

import com.alethanabooks.interfaces.Descargable;
import com.alethanabooks.interfaces.Validable;

public class LibroDigital extends Libro implements Descargable, Validable {

    private String rutaArchivo;
    private boolean disponibleDescarga;
    private String formato;

    public LibroDigital() {}

    public LibroDigital(String id, String titulo, String autor, String categoria,
                        double precio, int stock, String imagen,
                        String rutaArchivo, boolean disponibleDescarga, String formato) {
        super(id, titulo, autor, categoria, precio, stock, imagen);
        this.rutaArchivo = rutaArchivo;
        this.disponibleDescarga = disponibleDescarga;
        this.formato = formato;
    }

    @Override
    public String obtenerRutaDescarga() {
        return rutaArchivo;
    }

    @Override
    public boolean estaDisponibleParaDescarga() {
        return disponibleDescarga && rutaArchivo != null && !rutaArchivo.isBlank();
    }

    @Override
    public boolean esValido() {
        return getTitulo() != null && !getTitulo().isBlank()
                && getAutor() != null && !getAutor().isBlank()
                && getPrecio() > 0
                && formato != null && !formato.isBlank();
    }

    @Override
    public String obtenerMensajeError() {
        if (getTitulo() == null || getTitulo().isBlank()) return "El título no puede estar vacío.";
        if (getAutor() == null || getAutor().isBlank()) return "El autor no puede estar vacío.";
        if (getPrecio() <= 0) return "El precio debe ser mayor a cero.";
        if (formato == null || formato.isBlank()) return "El formato es obligatorio (PDF, EPUB, MOBI).";
        return "";
    }

    public String getRutaArchivo() { return rutaArchivo; }
    public String getFormato() { return formato; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public void setDisponibleDescarga(boolean disponibleDescarga) { this.disponibleDescarga = disponibleDescarga; }
    public void setFormato(String formato) { this.formato = formato; }
}