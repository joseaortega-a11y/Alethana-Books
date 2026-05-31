package com.alethanabooks.Util;

import com.alethanabooks.persistence.RutasDatos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImagenUtil {

    private ImagenUtil() {}

    public static StackPane crearPanelImagen(String nombreImagen, double ancho, double alto) {
        StackPane pane = new StackPane();
        pane.setPrefSize(ancho, alto);
        pane.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;");

        Image imagen = cargarImagen(nombreImagen);
        if (imagen != null) {
            ImageView iv = new ImageView(imagen);
            iv.setFitWidth(ancho - 8);
            iv.setFitHeight(alto - 8);
            iv.setPreserveRatio(true);
            pane.getChildren().add(iv);
        } else {
            Label emoji = new Label("");
            emoji.setStyle("-fx-font-size: " + (int)(alto / 4) + "px;");
            pane.getChildren().add(emoji);
        }
        return pane;
    }

    public static ImageView crearImageView(String nombreImagen, double ancho, double alto) {
        ImageView iv = new ImageView();
        iv.setFitWidth(ancho);
        iv.setFitHeight(alto);
        iv.setPreserveRatio(true);
        Image imagen = cargarImagen(nombreImagen);
        if (imagen != null) iv.setImage(imagen);
        return iv;
    }

    private static Image cargarImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isBlank()) return null;

        Path rutaExterna = Path.of(RutasDatos.CARPETA_IMAGENES + nombreImagen);
        if (Files.exists(rutaExterna)) {
            try (InputStream stream = new FileInputStream(rutaExterna.toFile())) {
                return new Image(stream);
            } catch (Exception ignored) {}
        }
        try (InputStream stream = ImagenUtil.class.getResourceAsStream("/imagenes/" + nombreImagen)) {
            if (stream != null) return new Image(stream);
        } catch (Exception ignored) {}

        return null;
    }
}