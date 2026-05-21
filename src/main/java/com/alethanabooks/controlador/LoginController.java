package com.alethanabooks.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtId;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtApodo;
    @FXML private TextField txtContra;

    @FXML private Button btnUsuario;
    @FXML private Button btnAdmin;


    @FXML
    private void onAdmin(ActionEvent event) {
        // Esta ruta ya estaba bien estructurada
        cambiarVentana(
                "/fxml/admin.fxml",
                "Alethana Books - Admin"
        );
    }

    @FXML
    private void onUsuario(ActionEvent event) {
        // Corregido: Se eliminó el paquete falso y se arregló el nombre del archivo (con guion medio)
        cambiarVentana(
                "/fxml/alethana-books.fxml",
                "Alethana Books - Catálogo"
        );
    }

    private void cambiarVentana(String rutaFXML, String tituloVentana) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(rutaFXML)
            );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle(tituloVentana);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.show();

            // cerrar ventana actual
            Stage actual = (Stage) btnAdmin.getScene().getWindow();
            actual.close();

        } catch (Exception e) {

            e.printStackTrace();

            mostrarError(
                    "Error",
                    "No se pudo abrir: " + rutaFXML
            );
        }
    }

    private void mostrarError(String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}