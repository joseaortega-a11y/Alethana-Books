package com.alethanabooks.controlador;

import com.alethanabooks.modelo.SesionActual;
import com.alethanabooks.modelo.*;
import com.alethanabooks.persistence.VentaRepository;
import com.alethanabooks.service.CarritoService;
import com.alethanabooks.service.CatalogoService;
import com.alethanabooks.strategy.DescuentoNormal;
import com.alethanabooks.strategy.DescuentoPromocional;
import com.alethanabooks.strategy.EstrategiaDescuento;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class CarritoController implements Initializable {

    @FXML private VBox listaItems;
    @FXML private Label lblSubtotal;
    @FXML private Label lblMontoDescuento;
    @FXML private Label lblTotal;
    @FXML private TextField txtCodigo;
    @FXML private Label lblDescuento;
    @FXML private RadioButton rbTarjeta;
    @FXML private RadioButton rbPSE;
    @FXML private RadioButton rbEfecty;

    private final CarritoService carritoService = SesionActual.getCarritoService();
    private final CatalogoService catalogoService = new CatalogoService();
    private final VentaRepository ventaRepository = new VentaRepository();

    private EstrategiaDescuento estrategia = new DescuentoNormal();
    private double porcentajeDescuento = 0;

    // Referencia al controlador del catálogo para refrescar el stock visualmente
    private InicioController inicioController;

    private static final java.util.Map<String, Double> CODIGOS = java.util.Map.of(
            "CYBER20",  0.20,
            "PROMO10",  0.10,
            "LECTOR15", 0.15
    );

    public void setInicioController(InicioController c) {
        this.inicioController = c;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ToggleGroup grupo = new ToggleGroup();
        rbTarjeta.setToggleGroup(grupo);
        rbPSE.setToggleGroup(grupo);
        rbEfecty.setToggleGroup(grupo);
        rbTarjeta.setSelected(true);

        renderizarItems();
        actualizarTotales();
    }

    private void renderizarItems() {
        listaItems.getChildren().clear();
        Carrito carrito = carritoService.getCarrito();
        if (carrito == null || carrito.getItems().isEmpty()) {
            Label lbl = new Label("Tu carrito está vacío.");
            lbl.setStyle("-fx-font-size: 15px;");
            lbl.setTextFill(Color.web("#94a3b8"));
            listaItems.getChildren().add(lbl);
            return;
        }
        for (ItemCarrito item : carrito.getItems()) {
            listaItems.getChildren().add(crearFilaItem(item));
        }
    }

    private HBox crearFilaItem(ItemCarrito item) {
        HBox fila = new HBox(14);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 10;");
        fila.setPadding(new Insets(12, 16, 12, 16));

        VBox info = new VBox(4);
        info.setMinWidth(200);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblTitulo = new Label(item.getLibro().getTitulo());
        lblTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        lblTitulo.setWrapText(true);

        Label lblAutor = new Label(item.getLibro().getAutor());
        lblAutor.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Label lblCantidad = new Label("Cantidad: " + item.getCantidad());
        lblCantidad.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        info.getChildren().addAll(lblTitulo, lblAutor, lblCantidad);

        Label lblSubtotalItem = new Label(String.format("COP %,.0f", item.calcularSubtotal()));
        lblSubtotalItem.setStyle("-fx-font-size: 15px; -fx-font-weight: 900; -fx-text-fill: #7c3aed;");

        Button btnQuitar = new Button("✕");
        btnQuitar.setStyle("-fx-background-color: #fee2e2; -fx-background-radius: 6; " +
                "-fx-font-weight: 800; -fx-font-size: 13px; -fx-padding: 4 8; -fx-text-fill: #ef4444;");
        btnQuitar.setOnAction(e -> {
            carritoService.eliminarLibro(item.getLibro().getId());
            renderizarItems();
            actualizarTotales();
        });

        fila.getChildren().addAll(info, lblSubtotalItem, btnQuitar);
        return fila;
    }

    @FXML
    private void onAplicarCodigo() {
        String codigo = txtCodigo.getText().trim().toUpperCase();
        if (CODIGOS.containsKey(codigo)) {
            porcentajeDescuento = CODIGOS.get(codigo);
            estrategia = new DescuentoPromocional(porcentajeDescuento);
            lblDescuento.setText("✓ Descuento del " + (int)(porcentajeDescuento * 100) + "% aplicado");
            lblDescuento.setTextFill(Color.web("#10b981"));
        } else {
            estrategia = new DescuentoNormal();
            porcentajeDescuento = 0;
            lblDescuento.setText("Código no válido.");
            lblDescuento.setTextFill(Color.web("#ef4444"));
        }
        actualizarTotales();
    }

    private void actualizarTotales() {
        Carrito carrito = carritoService.getCarrito();
        double subtotal = (carrito != null) ? carrito.calcularTotal() : 0;
        double total    = estrategia.aplicar(subtotal);
        double descuento = subtotal - total;

        lblSubtotal.setText(String.format("COP %,.0f", subtotal));
        lblMontoDescuento.setText(String.format("- COP %,.0f", descuento));
        lblTotal.setText(String.format("COP %,.0f", total));
    }

    @FXML
    private void onConfirmarCompra() {
        Carrito carrito = carritoService.getCarrito();
        if (carrito == null || carrito.getItems().isEmpty()) {
            mostrarAlerta("Carrito vacío", "Agrega libros antes de confirmar la compra.");
            return;
        }

        // Verificar stock antes de abrir pago
        for (ItemCarrito item : carrito.getItems()) {
            Libro libro = catalogoService.buscarPorId(item.getLibro().getId());
            if (libro != null && libro.getStock() < item.getCantidad()) {
                mostrarAlerta("Stock insuficiente",
                        "No hay suficiente stock de: " + libro.getTitulo() +
                                "\nDisponible: " + libro.getStock());
                return;
            }
        }

        String metodoPago = rbTarjeta.isSelected() ? "Tarjeta" :
                rbPSE.isSelected()     ? "PSE"     : "Efecty";
        double total = estrategia.aplicar(carrito.calcularTotal());

        // Abrir ventana de pago
        abrirVentanaPago(metodoPago, total, carrito);
    }

    private void abrirVentanaPago(String metodoPago, double total, Carrito carrito) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Pago.fxml"));
            Parent root = loader.load();
            PagoController pagoCtrl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Pasarela de Pago — Alethana Books");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Callback: se ejecuta si el pago fue aprobado
            pagoCtrl.inicializar(metodoPago, total, () -> {
                procesarCompraAprobada(carrito, metodoPago, total);
            });

            stage.showAndWait();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de pago: " + e.getMessage());
        }
    }

    private void procesarCompraAprobada(Carrito carrito, String metodoPago, double total) {
        // Reducir stock y guardar venta
        List<DetalleVenta> detalles = new ArrayList<>();
        for (ItemCarrito item : carrito.getItems()) {
            Libro libro = catalogoService.buscarPorId(item.getLibro().getId());
            if (libro != null) {
                libro.reducirStock(item.getCantidad());
                catalogoService.actualizar(libro);
                detalles.add(new DetalleVenta(libro, item.getCantidad()));
            }
        }

        Venta venta = new Venta(UUID.randomUUID().toString(),
                SesionActual.getUsuario(), detalles, metodoPago, total, "PAGADO");
        ventaRepository.guardar(venta);

        carritoService.vaciarCarrito();
        renderizarItems();
        actualizarTotales();

        // Refrescar tarjetas del catálogo para mostrar stock actualizado
        if (inicioController != null) {
            inicioController.refrescarCatalogo();
        }

        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ok.setTitle("¡Compra completada!");
        ok.setHeaderText(null);
        ok.setContentText("✅ Tu pedido fue registrado y pagado.\n" +
                "Método: " + metodoPago + "\nTotal: COP " + String.format("%,.0f", total));
        ok.showAndWait();
        ((Stage) lblTotal.getScene().getWindow()).close();
    }

    @FXML
    private void onCerrar() {
        ((Stage) lblTotal.getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}