package com.alethanabooks.strategy;

import com.alethanabooks.interfaces.Descargable;
import com.alethanabooks.modelo.ItemCarrito;
import com.alethanabooks.modelo.Carrito;

import java.util.List;

public class DescuentoDigital implements EstrategiaDescuento {

    private static final double PORCENTAJE_DIGITAL = 0.10;
    private final List<ItemCarrito> items;

    public DescuentoDigital(List<ItemCarrito> items) {
        this.items = items;
    }

    @Override
    public double aplicar(double precio) {
        if (items == null || items.isEmpty()) return precio;

        double totalFisicos = items.stream()
                .filter(i -> !(i.getLibro() instanceof Descargable))
                .mapToDouble(ItemCarrito::calcularSubtotal)
                .sum();

        double totalDigitales = items.stream()
                .filter(i -> i.getLibro() instanceof Descargable d && d.estaDisponibleParaDescarga())
                .mapToDouble(ItemCarrito::calcularSubtotal)
                .sum();

        return totalFisicos + totalDigitales * (1 - PORCENTAJE_DIGITAL);
    }

    @Override
    public double getPorcentaje() { return PORCENTAJE_DIGITAL; }

    @Override
    public String getDescripcion() {
        return "Descuento digital automático (−10% en e-books)";
    }
}