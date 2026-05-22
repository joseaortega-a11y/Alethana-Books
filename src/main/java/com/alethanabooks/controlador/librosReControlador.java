package com.alethanabooks.controlador;

import com.alethanabooks.factory.LibroFactory;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.service.CatalogoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class librosReControlador implements Initializable {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private FlowPane flowLibros;

    private final CatalogoService catalogoService = new CatalogoService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbCategoria.setItems(FXCollections.observableArrayList(AdminController.CATEGORIAS));
        cmbCategoria.setPromptText("Seleccionar categoría");
        cargarLibros();
    }

    @FXML
    private void onAgregar() {
        try {
            String titulo = txtTitulo.getText().trim();
            String autor = txtAutor.getText().trim();
            String categoria = cmbCategoria.getValue();
            String precioStr = txtPrecio.getText().trim();
            String stockStr = txtStock.getText().trim();

            if (titulo.isEmpty() || autor.isEmpty() || categoria == null) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.");
                return;
            }

            double precio = Double.parseDouble(precioStr);
            int stock = Integer.parseInt(stockStr);

            Libro nuevo = LibroFactory.crearLibro(
                    LibroFactory.TipoLibro.FISICO,
                    UUID.randomUUID().toString(),
                    titulo, autor, categoria, precio, stock, ""
            );

            catalogoService.agregar(nuevo);
            cargarLibros();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio y stock deben ser números válidos.");
        }
    }

    @FXML
    private void onGenerarRecomendacion() {
        List<Libro> recomendados = catalogoService.obtenerRecomendadosAleatorios(6);
        flowLibros.getChildren().clear();
        for (Libro libro : recomendados) {
            flowLibros.getChildren().add(crearTarjetaLibro(libro));
        }
    }

    private void cargarLibros() {
        flowLibros.getChildren().clear();
        for (Libro libro : catalogoService.obtenerTodos()) {
            flowLibros.getChildren().add(crearTarjetaLibro(libro));
        }
    }

    private VBox crearTarjetaLibro(Libro libro) {
        VBox card = new VBox(10);
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 14;");
        card.setPadding(new Insets(18));

        StackPane imgPane = new StackPane();
        imgPane.setPrefHeight(220);
        imgPane.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;");
        Label emoji = new Label("📚");
        emoji.setStyle("-fx-font-size: 54px;");
        imgPane.getChildren().add(emoji);

        Label lblTitulo = new Label(libro.getTitulo());
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: 800;");
        lblTitulo.setTextFill(Color.web("#0f172a"));
        lblTitulo.setWrapText(true);

        Label lblAutor = new Label(libro.getAutor());
        lblAutor.setStyle("-fx-font-size: 13px;");
        lblAutor.setTextFill(Color.web("#64748b"));

        Label lblCategoria = new Label(libro.getCategoria());
        lblCategoria.setStyle("-fx-background-color: #ede9fe; -fx-background-radius: 6; " +
                "-fx-font-size: 11px; -fx-font-weight: 700; -fx-padding: 3 8;");
        lblCategoria.setTextFill(Color.web("#7c3aed"));

        Label lblPrecio = new Label(String.format("COP %,.0f", libro.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 20px; -fx-font-weight: 900;");
        lblPrecio.setTextFill(Color.web("#7c3aed"));

        Button btnCarrito = new Button("Agregar al carrito");
        btnCarrito.setMaxWidth(Double.MAX_VALUE);
        btnCarrito.setTextFill(Color.WHITE);
        btnCarrito.setStyle("-fx-background-color: #7c3aed; -fx-background-radius: 10; " +
                "-fx-font-size: 13px; -fx-font-weight: 800;");

        card.getChildren().addAll(imgPane, lblTitulo, lblAutor, lblCategoria, lblPrecio, btnCarrito);
        return card;
    }

    private void limpiarCampos() {
        txtTitulo.clear();
        txtAutor.clear();
        cmbCategoria.setValue(null);
        cmbCategoria.setPromptText("Seleccionar categoría");
        txtPrecio.clear();
        txtStock.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}