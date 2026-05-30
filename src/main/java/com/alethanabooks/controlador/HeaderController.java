package com.alethanabooks.controlador;

import com.alethanabooks.modelo.SesionActual;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {
    @FXML private Button btnCerrarSesion;

    @FXML private TextField txtBusqueda;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void irInicio()            { navegar("/fxml/alethana-books.fxml",   "Alethana Books",               1280, 762); }
    @FXML private void irLibrosRecomendados(){ navegar("/fxml/librosRe.fxml",          "Alethana Books - Recomendados", 1280, 760); }
    @FXML private void irUltimosAniadidos()  { navegar("/fxml/UltimosAnadidos.fxml",       "Alethana Books - Últimos añadidos", 1280, 760); }
    @FXML private void irLibrosImportados()  { navegar("/fxml/LibrosImportados.fxml",  "Alethana Books - Importados",   1280, 760); }
    @FXML private void irOpiniones()         { navegar("/fxml/opiniones.fxml",         "Alethana Books - Opiniones",    1000, 700); }
    @FXML private void irLibrosDigitales()   { navegar("/fxml/LibrosDigitales.fxml",   "Alethana Books - Biblioteca Digital", 1280, 760); }


    @FXML
    private void onBuscar() {
        String texto = txtBusqueda.getText().trim();
        try {
            Stage stage = (Stage) txtBusqueda.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/alethana-books.fxml"));
            Parent root = loader.load();

            InicioController ctrl = loader.getController();
            ctrl.buscarDesdeHeader(texto);

            root.setOpacity(0);
            FadeTransition fi = new FadeTransition(Duration.millis(220), root);
            fi.setFromValue(0); fi.setToValue(1); fi.play();
            stage.getScene().setRoot(root);
            stage.setTitle("Alethana Books");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAbrirCarrito() {
        if (!SesionActual.haySesion()) {
            mostrarInfo("Inicia sesión", "Debes iniciar sesión para ver tu carrito.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Carrito.fxml"));
            Parent root = loader.load();
            CarritoController carritoCtrl = loader.getController();

            Object ctrl = com.alethanabooks.modelo.SesionActual.getCatalogoController();
            if (ctrl instanceof InicioController ic) {
                carritoCtrl.setInicioController(ic);
            }

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Mi Carrito");
            modal.setScene(new Scene(root, 860, 680));
            modal.setResizable(false);
            modal.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onAbrirCuenta() {
        if (!SesionActual.haySesion()) {
            mostrarInfo("Inicia sesión", "Debes iniciar sesión para ver tu cuenta.");
            return;
        }
        abrirVentanaModal("/fxml/Cuenta.fxml", "Mi Cuenta", 780, 640);
    }

    @FXML
    private void onAbrirAyuda() {
        Alert ayuda = new Alert(Alert.AlertType.INFORMATION);
        ayuda.setTitle("Centro de ayuda — Alethana Books");
        ayuda.setHeaderText("Preguntas frecuentes");
        ayuda.setContentText(
                "¿Cómo compro un libro?\n" +
                        "  → Agrégalo al carrito y confirma la compra en el carrito.\n\n" +
                        "¿Qué métodos de pago hay?\n" +
                        "  → Tarjeta crédito/débito, PSE y Efecty/Baloto.\n\n" +
                        "¿Los libros digitales cómo los descargo?\n" +
                        "  → Después de comprar aparece la ruta de descarga en tu cuenta.\n\n" +
                        "¿Cómo uso un código de descuento?\n" +
                        "  → Escríbelo en el campo 'Código de descuento' dentro del carrito.\n\n"
        );
        ayuda.showAndWait();
    }


    private void abrirVentanaModal(String fxml, String titulo, double ancho, double alto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle(titulo);
            modal.setScene(new Scene(root, ancho, alto));
            modal.setResizable(false);
            modal.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void navegar(String fxml, String titulo, double ancho, double alto) {
        try {
            Stage stage = (Stage) txtBusqueda.getScene().getWindow();
            Parent rootActual = stage.getScene().getRoot();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent rootNuevo = loader.load();
            rootNuevo.setOpacity(0);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(180), rootActual);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                stage.getScene().setRoot(rootNuevo);
                stage.setTitle(titulo);
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

    private void mostrarInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    @FXML
    private void onCerrar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Alethana Books - Login");
            stage.setScene(new Scene(root, 492, 572));
            stage.setResizable(false);
            stage.show();
            ((Stage) btnCerrarSesion.getScene().getWindow()).close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}