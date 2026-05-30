package com.alethanabooks.strategy;

public class DescuentoFijo implements EstrategiaDescuento {

    private final double monto;
    private final String descripcion;

    public DescuentoFijo(double monto, String descripcion) {
        this.monto       = monto;
        this.descripcion = descripcion;
    }

    @Override
    public double aplicar(double precio) {
        return Math.max(0, precio - monto);
    }

    @Override
    public double getPorcentaje() { return 0.0; }

    @Override
    public String getDescripcion() {
        return descripcion + " (−COP " + String.format("%,.0f", monto) + ")";
    }
}