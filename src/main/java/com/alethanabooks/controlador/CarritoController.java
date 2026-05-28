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
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

    // Códigos válidos: código → porcentaje
    private static final java.util.Map<String, Double> CODIGOS = java.util.Map.of(
            "CYBER20",  0.20,
            "PROMO10",  0.10,
            "LECTOR15", 0.15
    );

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

        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblTitulo = new Label(item.getLibro().getTitulo());
        lblTitulo.setStyle("-fx-font-size: 14px; -fx-font-weight: 800;");
        lblTitulo.setTextFill(Color.web("#0f172a"));

        Label lblAutor = new Label(item.getLibro().getAutor());
        lblAutor.setStyle("-fx-font-size: 12px;");
        lblAutor.setTextFill(Color.web("#64748b"));

        Label lblCantidad = new Label("Cantidad: " + item.getCantidad());
        lblCantidad.setStyle("-fx-font-size: 12px;");
        lblCantidad.setTextFill(Color.web("#64748b"));

        info.getChildren().addAll(lblTitulo, lblAutor, lblCantidad);

        Label lblSubtotalItem = new Label(String.format("COP %,.0f", item.calcularSubtotal()));
        lblSubtotalItem.setStyle("-fx-font-size: 15px; -fx-font-weight: 900;");
        lblSubtotalItem.setTextFill(Color.web("#7c3aed"));

        Button btnQuitar = new Button("✕");
        btnQuitar.setStyle("-fx-background-color: #fee2e2; -fx-background-radius: 6; " +
                "-fx-font-weight: 800; -fx-font-size: 13px; -fx-padding: 4 8;");
        btnQuitar.setTextFill(Color.web("#ef4444"));
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
        double total = estrategia.aplicar(subtotal);
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

        String metodoPago = rbTarjeta.isSelected() ? "Tarjeta" :
                rbPSE.isSelected()     ? "PSE"     : "Efecty";

        // Crear detalles y reducir stock
        List<DetalleVenta> detalles = new ArrayList<>();
        for (ItemCarrito item : carrito.getItems()) {
            Libro libro = catalogoService.buscarPorId(item.getLibro().getId());
            if (libro != null) {
                int cantidad = item.getCantidad();
                if (libro.getStock() < cantidad) {
                    mostrarAlerta("Stock insuficiente",
                            "No hay suficiente stock de: " + libro.getTitulo());
                    return;
                }
                libro.reducirStock(cantidad);
                catalogoService.actualizar(libro);
                detalles.add(new DetalleVenta(libro, cantidad));
            }
        }

        double total = estrategia.aplicar(carrito.calcularTotal());
        Venta venta = new Venta(UUID.randomUUID().toString(),
                SesionActual.getUsuario(), detalles, metodoPago, total);
        ventaRepository.guardar(venta);

        carritoService.vaciarCarrito();
        renderizarItems();
        actualizarTotales();

        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ok.setTitle("¡Compra confirmada!");
        ok.setHeaderText(null);
        ok.setContentText("Tu pedido fue registrado.\nMétodo de pago: " + metodoPago +
                "\nTotal: COP " + String.format("%,.0f", total));
        ok.showAndWait();
        ((Stage) lblTotal.getScene().getWindow()).close();
    }

    @FXML
    private void onCerrar() {
        ((Stage) lblTotal.getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}