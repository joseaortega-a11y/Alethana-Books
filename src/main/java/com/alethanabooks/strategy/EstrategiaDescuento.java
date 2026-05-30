package com.alethanabooks.strategy;

public interface EstrategiaDescuento {
    double aplicar(double precio);

    default double getPorcentaje() { return 0.0; }
    default String getDescripcion() { return "Sin descuento"; }
}