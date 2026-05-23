package com.alethanabooks.util;

import com.alethanabooks.persistence.RutasDatos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

import java.io.File;

public class ImagenUtil {

    private ImagenUtil() {}

    /**
     * Devuelve un StackPane con la imagen del libro si existe,
     * o un emoji 📚 como fallback si no hay imagen.
     */
    public static StackPane crearPanelImagen(String nombreImagen, double ancho, double alto) {
        StackPane pane = new StackPane();
        pane.setPrefSize(ancho, alto);
        pane.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;");

        if (nombreImagen != null && !nombreImagen.isBlank()) {
            File archivo = new File(RutasDatos.CARPETA_IMAGENES + nombreImagen);
            if (archivo.exists()) {
                ImageView iv = new ImageView(new Image(archivo.toURI().toString()));
                iv.setFitWidth(ancho - 8);
                iv.setFitHeight(alto - 8);
                iv.setPreserveRatio(true);
                pane.getChildren().add(iv);
                return pane;
            }
        }

        // Fallback: emoji
        Label emoji = new Label("Sin imagen");
        emoji.setStyle("-fx-font-size: " + (alto / 4) + "px;");
        pane.getChildren().add(emoji);
        return pane;
    }
}