package com.alethanabooks.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private String id;
    private Usuario usuario;
    private LocalDateTime fecha;
    private List<DetalleVenta> detalles = new ArrayList<>();
    private double total;
    private String metodoPago;

    public Venta() {}

    public Venta(String id, Usuario usuario, List<DetalleVenta> detalles,
                 String metodoPago, double total) {
        this.id = id;
        this.usuario = usuario;
        this.fecha = LocalDateTime.now();
        this.detalles = detalles;
        this.metodoPago = metodoPago;
        this.total = total;
    }

    public String getId()               { return id; }
    public Usuario getUsuario()         { return usuario; }
    public LocalDateTime getFecha()     { return fecha; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public double getTotal()            { return total; }
    public String getMetodoPago()       { return metodoPago; }
}