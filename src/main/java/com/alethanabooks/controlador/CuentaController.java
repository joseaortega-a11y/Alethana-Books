package com.alethanabooks.controlador;

import com.alethanabooks.modelo.SesionActual;
import com.alethanabooks.modelo.DetalleVenta;
import com.alethanabooks.modelo.Usuario;
import com.alethanabooks.modelo.Venta;
import com.alethanabooks.persistence.VentaRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CuentaController implements Initializable {

    @FXML private Label lblBienvenida;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtTelefono;
    @FXML private Label lblGuardado;
    @FXML private VBox listaCompras;

    private final VentaRepository ventaRepository = new VentaRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario u = SesionActual.getUsuario();
        if (u != null) {
            lblBienvenida.setText("Hola, " + u.getNombre() + "  ·  " + u.getCorreo());
            txtNombreCompleto.setText(u.getNombre());
        }
        cargarHistorial();
    }

    @FXML
    private void onGuardarEnvio() {
        String nombre    = txtNombreCompleto.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String ciudad    = txtCiudad.getText().trim();
        String telefono  = txtTelefono.getText().trim();

        if (nombre.isEmpty() || direccion.isEmpty() || ciudad.isEmpty() || telefono.isEmpty()) {
            lblGuardado.setText("Completa todos los campos de envío.");
            lblGuardado.setTextFill(Color.web("#ef4444"));
            return;
        }
        if (!telefono.matches("\\d{7,15}")) {
            lblGuardado.setText("El teléfono debe tener solo dígitos (7-15).");
            lblGuardado.setTextFill(Color.web("#ef4444"));
            return;
        }
        lblGuardado.setText("✓ Datos de envío guardados correctamente.");
        lblGuardado.setTextFill(Color.web("#10b981"));
    }

    private void cargarHistorial() {
        listaCompras.getChildren().clear();
        Usuario u = SesionActual.getUsuario();
        if (u == null) return;

        List<Venta> ventas = ventaRepository.obtenerTodas().stream()
                .filter(v -> v.getUsuario() != null &&
                        v.getUsuario().getCorreo().equalsIgnoreCase(u.getCorreo()))
                .collect(Collectors.toList());

        if (ventas.isEmpty()) {
            Label lbl = new Label("Aún no tienes compras registradas.");
            lbl.setStyle("-fx-font-size: 14px;");
            lbl.setTextFill(Color.web("#94a3b8"));
            listaCompras.getChildren().add(lbl);
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Venta v : ventas) {
            VBox card = new VBox(6);
            card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; " +
                    "-fx-border-color: #e5e7eb; -fx-border-radius: 10;");
            card.setPadding(new Insets(12, 16, 12, 16));

            String fecha = (v.getFecha() != null) ? v.getFecha().format(fmt) : "—";
            Label lblFecha = new Label("📅 " + fecha + "   💳 " + v.getMetodoPago());
            lblFecha.setStyle("-fx-font-size: 12px;");
            lblFecha.setTextFill(Color.web("#64748b"));

            for (DetalleVenta d : v.getDetalles()) {
                Label l = new Label("  • " + d.getLibro().getTitulo() +
                        " x" + d.getCantidad() +
                        "  →  COP " + String.format("%,.0f", d.getSubtotal()));
                l.setStyle("-fx-font-size: 13px;");
                l.setTextFill(Color.web("#0f172a"));
                card.getChildren().add(l);
            }

            Label lblTotal = new Label("Total: COP " + String.format("%,.0f", v.getTotal()));
            lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: 900;");
            lblTotal.setTextFill(Color.web("#7c3aed"));

            card.getChildren().addAll(lblFecha, lblTotal);
            listaCompras.getChildren().add(card);
        }
    }
}