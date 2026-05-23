package com.alethanabooks.controlador;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @FXML private TextField txtBusqueda;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void irInicio()            { navegar("/fxml/alethana-books.fxml",   "Alethana Books",               1280, 762); }
    @FXML private void irLibrosRecomendados(){ navegar("/fxml/librosRe.fxml",          "Alethana Books - Recomendados", 1280, 760); }
    @FXML private void irMasVendidos()       { navegar("/fxml/masVendidos.fxml",       "Alethana Books - Más Vendidos", 1280, 760); }
    @FXML private void irLibrosImportados()  { navegar("/fxml/LibrosImportados.fxml",  "Alethana Books - Importados",   1280, 760); }
    @FXML private void onBuscar()            { irInicio(); }

    private void navegar(String fxml, String titulo, double ancho, double alto) {
        try {
            Stage stage = (Stage) txtBusqueda.getScene().getWindow();
            Parent rootActual = stage.getScene().getRoot();

            // Cargar el nuevo contenido antes de animar
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent rootNuevo = loader.load();
            rootNuevo.setOpacity(0);

            // Fade OUT del contenido actual
            FadeTransition fadeOut = new FadeTransition(Duration.millis(180), rootActual);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                // Swap de escena en el mismo Stage — sin parpadeo
                stage.getScene().setRoot(rootNuevo);
                stage.setTitle(titulo);

                // Fade IN del nuevo contenido
                FadeTransition fadeIn = new FadeTransition(Duration.millis(220), rootNuevo);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}