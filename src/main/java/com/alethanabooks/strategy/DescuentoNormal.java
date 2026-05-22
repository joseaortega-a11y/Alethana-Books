package com.alethanabooks.strategy;

public class DescuentoNormal implements EstrategiaDescuento {

    @Override
    public double aplicar(double precio) {
        return precio;
    }
}