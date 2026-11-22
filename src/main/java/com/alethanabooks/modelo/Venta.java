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
    private String estadoPago;
    private DatosEnvio datosEnvio;

    // Campos de la tabla venta
    private double subtotal;
    private double descuento;
    private String tipoDescuento = "NORMAL";   // NORMAL, PROMOCIONAL, DIGITAL, FIJO
    private String codigo;                     // código de descuento aplicado (puede ser null)

    public Venta() {}

    public Venta(String id, Usuario usuario, List<DetalleVenta> detalles,
                 String metodoPago, double total, String estadoPago,
                 DatosEnvio datosEnvio) {
        this.id          = id;
        this.usuario     = usuario;
        this.fecha       = LocalDateTime.now();
        this.detalles    = detalles;
        this.metodoPago  = metodoPago;
        this.total       = total;
        this.estadoPago  = estadoPago;
        this.datosEnvio  = datosEnvio;
        this.subtotal    = detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        this.descuento   = Math.max(0, subtotal - total);
    }

    public String getId()                   { return id; }
    public Usuario getUsuario()             { return usuario; }
    public LocalDateTime getFecha()         { return fecha; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public double getTotal()                { return total; }
    public String getMetodoPago()           { return metodoPago; }
    public String getEstadoPago()           { return estadoPago != null ? estadoPago : "PENDIENTE"; }
    public DatosEnvio getDatosEnvio()       { return datosEnvio; }
    public double getSubtotal()             { return subtotal; }
    public double getDescuento()            { return descuento; }
    public String getTipoDescuento()        { return tipoDescuento; }
    public String getCodigo()               { return codigo; }

    public void setId(String id)                       { this.id = id; }
    public void setFecha(LocalDateTime fecha)          { this.fecha = fecha; }
    public void setSubtotal(double subtotal)           { this.subtotal = subtotal; }
    public void setDescuento(double descuento)         { this.descuento = descuento; }
    public void setTipoDescuento(String tipoDescuento) { this.tipoDescuento = tipoDescuento; }
    public void setCodigo(String codigo)               { this.codigo = codigo; }
}
