package com.alethanabooks.controlador;

import com.alethanabooks.modelo.Rol;
import com.alethanabooks.modelo.SesionActual;
import com.alethanabooks.modelo.Usuario;
import com.alethanabooks.service.UsuarioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtApodo;
    @FXML private PasswordField txtContra;
    @FXML private Button btnUsuario;
    @FXML private Button btnAdmin;

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lambda: inicializar admin por defecto al arrancar
        Runnable inicializar = () -> usuarioService.inicializarAdminPorDefecto();
        inicializar.run();

        // Lambda: validar campos en tiempo real
        txtCorreo.textProperty().addListener((obs, old, nuevo) -> {
            boolean valido = nuevo.contains("@") && nuevo.contains(".");
            txtCorreo.setStyle(valido || nuevo.isEmpty()
                    ? "-fx-background-radius: 8;"
                    : "-fx-background-radius: 8; -fx-border-color: #ef4444;");
        });
    }

    @FXML
    private void onUsuario(ActionEvent event) {
        String correo = txtCorreo.getText().trim();
        String nombre = txtApodo.getText().trim();
        String contra = txtContra.getText();

        if (correo.isEmpty() || nombre.isEmpty() || contra.isEmpty()) {
            mostrarError("Campos incompletos", "Por favor completa correo, nombre y contraseña.");
            return;
        }

        // Buscar usuario existente; si no existe, registrar y luego autenticar
        Optional<Usuario> usuario = usuarioService.autenticar(correo, contra);
        if (usuario.isEmpty()) {
            usuarioService.registrar(nombre, correo, contra);
            usuario = usuarioService.autenticar(correo, contra);
        }

        if (usuario.isEmpty()) {
            mostrarError("Error de acceso", "No se pudo autenticar. Verifica tu contraseña.");
            return;
        }

        // Iniciar sesión con el usuario autenticado
        SesionActual.iniciar(usuario.get());

        cambiarVentana("/fxml/alethana-books.fxml", "Alethana Books - Catálogo");
    }

    @FXML
    private void onAdmin(ActionEvent event) {
        String correo = txtCorreo.getText().trim();
        String contra = txtContra.getText();

        if (correo.isEmpty() || contra.isEmpty()) {
            mostrarError("Campos incompletos", "Ingresa correo y contraseña de administrador.");
            return;
        }

        boolean esAdmin = usuarioService.esAdmin(correo, contra);
        if (!esAdmin) {
            mostrarError("Acceso denegado", "Credenciales incorrectas o no eres administrador.");
            return;
        }

        cambiarVentana("/fxml/admin.fxml", "Alethana Books - Admin");
    }

    private void cambiarVentana(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            ((Stage) btnAdmin.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo abrir: " + rutaFXML);
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}