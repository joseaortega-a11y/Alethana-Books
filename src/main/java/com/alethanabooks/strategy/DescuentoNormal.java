package com.alethanabooks.strategy;

public class DescuentoNormal implements EstrategiaDescuento {

    @Override
    public double aplicar(double precio) { return precio; }

    @Override
    public double getPorcentaje() { return 0.0; }

    @Override
    public String getDescripcion() { return "Sin descuento"; }
}