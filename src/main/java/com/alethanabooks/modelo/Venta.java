package com.alethanabooks.modelo;

import java.util.ArrayList;
import java.util.List;

public class Venta {
    private String id;
    private Cliente cliente;
    private List<DetalleVenta> detalles;

    public Venta(String id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.detalles = new ArrayList<>();
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
    }

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
}