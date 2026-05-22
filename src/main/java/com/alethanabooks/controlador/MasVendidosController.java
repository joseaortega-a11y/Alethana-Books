package com.alethanabooks.controlador;

import com.alethanabooks.factory.LibroFactory;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.service.CatalogoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class MasVendidosController implements Initializable {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private VBox listaLibros;

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

    private void cargarLibros() {
        listaLibros.getChildren().clear();
        List<Libro> libros = catalogoService.obtenerTodos();
        String[] colores = {"#f59e0b", "#94a3b8", "#cd7c3e", "#10b981", "#7c3aed"};

        for (int i = 0; i < libros.size(); i++) {
            String color = colores[Math.min(i, colores.length - 1)];
            listaLibros.getChildren().add(crearFilaLibro(libros.get(i), i + 1, color));
        }
    }

    private HBox crearFilaLibro(Libro libro, int posicion, String colorPos) {
        HBox fila = new HBox(24);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color: white; -fx-background-radius: 16; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 16; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 2);");
        fila.setPadding(new Insets(20, 24, 20, 24));

        Label lblNum = new Label("#" + posicion);
        lblNum.setStyle("-fx-font-size: 36px; -fx-font-weight: 900;");
        lblNum.setTextFill(Color.web(colorPos));
        lblNum.setPrefWidth(60);

        StackPane imgPane = new StackPane();
        imgPane.setPrefSize(95, 140);
        imgPane.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;");
        Label emoji = new Label("📚");
        emoji.setStyle("-fx-font-size: 40px;");
        imgPane.getChildren().add(emoji);

        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblTitulo = new Label(libro.getTitulo());
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: 900;");
        lblTitulo.setTextFill(Color.web("#0f172a"));

        Label lblAutor = new Label(libro.getAutor());
        lblAutor.setStyle("-fx-font-size: 14px;");
        lblAutor.setTextFill(Color.web("#64748b"));

        Label lblCategoria = new Label(libro.getCategoria());
        lblCategoria.setStyle("-fx-background-color: #ede9fe; -fx-background-radius: 8; " +
                "-fx-font-size: 12px; -fx-font-weight: 700; -fx-padding: 4 12;");
        lblCategoria.setTextFill(Color.web("#7c3aed"));

        info.getChildren().addAll(lblTitulo, lblAutor, lblCategoria);

        VBox acciones = new VBox(8);
        acciones.setAlignment(Pos.CENTER_RIGHT);

        Label lblPrecio = new Label(String.format("COP %,.0f", libro.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 22px; -fx-font-weight: 900;");
        lblPrecio.setTextFill(Color.web("#7c3aed"));

        Button btnCarrito = new Button("Agregar al carrito");
        btnCarrito.setTextFill(Color.WHITE);
        btnCarrito.setStyle("-fx-background-color: #7c3aed; -fx-background-radius: 10; " +
                "-fx-font-size: 13px; -fx-font-weight: 800; -fx-padding: 9 22;");

        acciones.getChildren().addAll(lblPrecio, btnCarrito);
        fila.getChildren().addAll(lblNum, imgPane, info, acciones);
        return fila;
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