package com.alethanabooks.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private String id;
    private Cliente cliente;
    private LocalDate fecha;
    private List<DetalleVenta> detalles;

    public Venta(String id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = LocalDate.now();
        this.detalles = new ArrayList<>();
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
    }

    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetalleVenta::calcularSubtotal)
                .sum();
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public LocalDate getFecha() { return fecha; }
    public List<DetalleVenta> getDetalles() { return detalles; }
}