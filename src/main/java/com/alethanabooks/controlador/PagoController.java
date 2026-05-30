package com.alethanabooks.controlador;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controlador de simulación de pago.
 * Muestra un formulario diferente según el método elegido,
 * simula el procesamiento y notifica si fue aprobado o rechazado.
 */
public class PagoController {

    @FXML private Label      lblMetodo;
    @FXML private Label      lblTotal;
    @FXML private VBox       panelTarjeta;
    @FXML private VBox       panelPSE;
    @FXML private VBox       panelEfecty;
    @FXML private TextField  txtNumero;
    @FXML private TextField  txtNombre;
    @FXML private TextField  txtExpiry;
    @FXML private TextField  txtCVV;
    @FXML private ComboBox<String> cmbBanco;
    @FXML private TextField  txtCedulaPSE;
    @FXML private Label      lblCodigoEfecty;
    @FXML private Button     btnPagar;
    @FXML private Label      lblEstado;
    @FXML private ProgressBar barProgreso;

    private String metodoPago;
    private double total;
    private boolean aprobado = false;

    // Callback que llama CarritoController al cerrarse esta ventana
    private Runnable onPagoAprobado;

    public void inicializar(String metodoPago, double total, Runnable onPagoAprobado) {
        this.metodoPago      = metodoPago;
        this.total           = total;
        this.onPagoAprobado  = onPagoAprobado;

        lblMetodo.setText(metodoPago);
        lblTotal.setText(String.format("COP %,.0f", total));

        // Mostrar solo el panel correspondiente
        panelTarjeta.setVisible(false); panelTarjeta.setManaged(false);
        panelPSE.setVisible(false);     panelPSE.setManaged(false);
        panelEfecty.setVisible(false);  panelEfecty.setManaged(false);

        switch (metodoPago) {
            case "Tarjeta" -> {
                panelTarjeta.setVisible(true); panelTarjeta.setManaged(true);
            }
            case "PSE" -> {
                panelPSE.setVisible(true); panelPSE.setManaged(true);
                cmbBanco.getItems().addAll(
                        "Bancolombia", "Davivienda", "BBVA", "Banco de Bogotá",
                        "Nequi", "Banco Popular", "Itaú", "Scotiabank Colpatria"
                );
                cmbBanco.setPromptText("Seleccionar banco");
            }
            case "Efecty" -> {
                panelEfecty.setVisible(true); panelEfecty.setManaged(true);
                // Generar código único de 8 dígitos
                String codigo = String.format("%08d", (int)(Math.random() * 99_999_999));
                lblCodigoEfecty.setText("# " + codigo);
            }
        }
    }

    @FXML
    private void onPagar() {
        if (!validarCampos()) return;

        btnPagar.setDisable(true);
        lblEstado.setText("⏳ Procesando pago...");
        lblEstado.setTextFill(Color.web("#f59e0b"));
        barProgreso.setVisible(true);

        // Simular delay de procesamiento (2 segundos)
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            // Simular: 90% de éxito, 10% rechazo (para que sea realista)
            boolean exito = Math.random() > 0.1;

            if (exito) {
                aprobado = true;
                lblEstado.setText("✅ ¡Pago aprobado!");
                lblEstado.setTextFill(Color.web("#10b981"));
                barProgreso.setVisible(false);

                // Cerrar y notificar tras 1.2 segundos
                Timeline cierre = new Timeline(new KeyFrame(Duration.seconds(1.2), ev -> {
                    if (onPagoAprobado != null) onPagoAprobado.run();
                    cerrar();
                }));
                cierre.play();
            } else {
                aprobado = false;
                lblEstado.setText("❌ Pago rechazado. Verifica tus datos e intenta de nuevo.");
                lblEstado.setTextFill(Color.web("#ef4444"));
                barProgreso.setVisible(false);
                btnPagar.setDisable(false);
            }
        }));
        timeline.play();
    }

    private boolean validarCampos() {
        lblEstado.setText("");
        return switch (metodoPago) {
            case "Tarjeta" -> validarTarjeta();
            case "PSE"     -> validarPSE();
            case "Efecty"  -> true; // Efecty solo muestra código, no requiere entrada
            default        -> true;
        };
    }

    private boolean validarTarjeta() {
        String num    = txtNumero.getText().replaceAll("\\s", "");
        String nombre = txtNombre.getText().trim();
        String expiry = txtExpiry.getText().trim();
        String cvv    = txtCVV.getText().trim();

        if (num.length() != 16 || !num.matches("\\d+")) {
            error("El número de tarjeta debe tener 16 dígitos."); return false;
        }
        if (nombre.isEmpty()) {
            error("Ingresa el nombre del titular."); return false;
        }
        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            error("Fecha de vencimiento inválida. Usa MM/AA (ej: 08/27)"); return false;
        }
        if (!cvv.matches("\\d{3,4}")) {
            error("CVV inválido. Debe tener 3 o 4 dígitos."); return false;
        }
        return true;
    }

    private boolean validarPSE() {
        if (cmbBanco.getValue() == null) {
            error("Selecciona tu banco."); return false;
        }
        String cedula = txtCedulaPSE.getText().trim();
        if (cedula.isEmpty() || !cedula.matches("\\d{6,12}")) {
            error("Ingresa un número de documento válido."); return false;
        }
        return true;
    }

    private void error(String msg) {
        lblEstado.setText("⚠ " + msg);
        lblEstado.setTextFill(Color.web("#ef4444"));
    }

    @FXML
    private void onCancelar() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) btnPagar.getScene().getWindow()).close();
    }

    public boolean isPagoAprobado() { return aprobado; }
}