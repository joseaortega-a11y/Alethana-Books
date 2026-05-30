package com.alethanabooks.strategy;

public class DescuentoPromocional implements EstrategiaDescuento {

    private final double porcentaje;
    private final String codigo;

    public DescuentoPromocional(double porcentaje, String codigo) {
        this.porcentaje = porcentaje;
        this.codigo     = codigo;
    }

    public DescuentoPromocional(double porcentaje) {
        this(porcentaje, "PROMO");
    }

    @Override
    public double aplicar(double precio) {
        return precio - (precio * porcentaje);
    }

    @Override
    public double getPorcentaje() { return porcentaje; }

    @Override
    public String getDescripcion() {
        return "Código " + codigo + " (−" + (int)(porcentaje * 100) + "%)";
    }
}