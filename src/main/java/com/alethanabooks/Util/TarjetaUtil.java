package com.alethanabooks.Util;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

/** Ayudas comunes para las tarjetas de libros de los catálogos. */
public class TarjetaUtil {

    /** Color único del botón "Agregar al carrito". */
    public static final String COLOR_BOTON = "#7c3aed";

    private TarjetaUtil() {}

    /**
     * Hace que la tarjeta reparta el ancho del panel para que quepan
     * exactamente {@code porFila} tarjetas por fila, sin dejar hueco a la derecha.
     */
    public static void anchoAdaptable(Region tarjeta, FlowPane panel, int porFila, double anchoMinimo) {
        tarjeta.prefWidthProperty().bind(Bindings.max(anchoMinimo,
                panel.widthProperty()
                        .subtract(panel.getHgap() * (porFila - 1))
                        .divide(porFila)
                        .subtract(1)));
    }

    /** Deja el texto en una sola línea y lo corta con "..." si no cabe. */
    public static void unaLinea(Label label) {
        label.setWrapText(false);
        label.setTextOverrun(OverrunStyle.ELLIPSIS);
        label.setMaxWidth(Double.MAX_VALUE);
    }

    /** Igual que {@link #unaLinea}, y muestra el texto completo al pasar el mouse. */
    public static void tituloUnaLinea(Label label) {
        unaLinea(label);
        label.setTooltip(new Tooltip(label.getText()));
    }
}
