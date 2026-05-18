package com.alethanabooks.strategy;

public class DescuentoPromocional implements EstrategiaDescuento {
    private double porcentaje;

    public DescuentoPromocional(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public double aplicar(double precio) {
        return precio - (precio * porcentaje / 100);
    }
}