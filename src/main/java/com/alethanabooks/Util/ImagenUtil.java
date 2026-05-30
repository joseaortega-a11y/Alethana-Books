package com.alethanabooks.Util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

import java.io.InputStream;

public class ImagenUtil {

    private ImagenUtil() {}


    public static StackPane crearPanelImagen(String nombreImagen, double ancho, double alto) {
        StackPane pane = new StackPane();
        pane.setPrefSize(ancho, alto);
        pane.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;");

        if (nombreImagen != null && !nombreImagen.isBlank()) {
            InputStream stream = ImagenUtil.class.getResourceAsStream("/imagenes/" + nombreImagen);
            if (stream != null) {
                ImageView iv = new ImageView(new Image(stream));
                iv.setFitWidth(ancho - 8);
                iv.setFitHeight(alto - 8);
                iv.setPreserveRatio(true);
                pane.getChildren().add(iv);
                return pane;
            }
        }

        Label emoji = new Label("");
        emoji.setStyle("-fx-font-size: " + (int)(alto / 4) + "px;");
        pane.getChildren().add(emoji);
        return pane;
    }
}
