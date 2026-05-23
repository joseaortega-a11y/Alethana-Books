package com.alethanabooks.controlador;

import com.alethanabooks.factory.LibroFactory;
import com.alethanabooks.functional.FiltroLibro;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.persistence.RutasDatos;
import com.alethanabooks.service.CatalogoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class AdminController implements Initializable {

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, String> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, Double> colPrecio;
    @FXML private TableColumn<Libro, Integer> colStock;
    @FXML private TableColumn<Libro, String> colImagen;

    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private TextField txtBuscar;
    @FXML private Label lblRutaImagen;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;

    private final CatalogoService catalogoService = new CatalogoService();
    private ObservableList<Libro> librosObservable;

    // Ruta del archivo de imagen seleccionado (ya copiado a resources)
    private String imagenSeleccionada = "";

    public static final List<String> CATEGORIAS = List.of(
            "Artes",
            "Biografias y literatura",
            "Ciencia",
            "Tecnologia",
            "Negocios y finanzas",
            "Ficcion",
            "Filosofia",
            "Historia",
            "Literatura juvenil"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colImagen.setCellValueFactory(new PropertyValueFactory<>("imagen"));

        cmbCategoria.setItems(FXCollections.observableArrayList(CATEGORIAS));
        cmbCategoria.setPromptText("Seleccionar categoría");

        cargarLibros();

        txtBuscar.textProperty().addListener((obs, old, texto) -> {
            FiltroLibro filtro = libro -> libro.coincideCon(texto);
            List<Libro> filtrados = catalogoService.obtenerTodos().stream()
                    .filter(filtro::filtrar)
                    .toList();
            tablaLibros.setItems(FXCollections.observableArrayList(filtrados));
        });

        btnEliminar.disableProperty().bind(
                tablaLibros.getSelectionModel().selectedItemProperty().isNull()
        );
    }

    @FXML
    private void onSeleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar portada del libro");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        File archivoElegido = fileChooser.showOpenDialog(btnAgregar.getScene().getWindow());
        if (archivoElegido == null) return;

        try {
            // Copiar la imagen a la carpeta de recursos del proyecto
            Path destino = Path.of(RutasDatos.CARPETA_IMAGENES + archivoElegido.getName());
            Files.createDirectories(destino.getParent());
            Files.copy(archivoElegido.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            // Guardar solo el nombre del archivo
            imagenSeleccionada = archivoElegido.getName();
            lblRutaImagen.setText( archivoElegido.getName());
            lblRutaImagen.setStyle("-fx-font-size: 12px; -fx-text-fill: #10b981;");

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo copiar la imagen: " + e.getMessage());
        }
    }

    @FXML
    private void onAgregar() {
        try {
            String titulo    = txtTitulo.getText().trim();
            String autor     = txtAutor.getText().trim();
            String categoria = cmbCategoria.getValue();
            String precioStr = txtPrecio.getText().trim();
            String stockStr  = txtStock.getText().trim();

            if (titulo.isEmpty() || autor.isEmpty() || categoria == null || categoria.isEmpty()) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.");
                return;
            }

            double precio = Double.parseDouble(precioStr);
            int stock = Integer.parseInt(stockStr);

            Libro nuevo = LibroFactory.crearLibro(
                    LibroFactory.TipoLibro.FISICO,
                    UUID.randomUUID().toString(),
                    titulo, autor, categoria, precio, stock,
                    imagenSeleccionada   // nombre del archivo, vacío si no eligió
            );

            catalogoService.agregar(nuevo);
            cargarLibros();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio y stock deben ser números válidos.");
        }
    }

    @FXML
    private void onEliminar() {
        Libro seleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar \"" + seleccionado.getTitulo() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                catalogoService.eliminar(seleccionado.getId());
                cargarLibros();
            }
        });
    }

    private void cargarLibros() {
        librosObservable = FXCollections.observableArrayList(catalogoService.obtenerTodos());
        tablaLibros.setItems(librosObservable);
    }

    private void limpiarCampos() {
        txtTitulo.clear();
        txtAutor.clear();
        cmbCategoria.setValue(null);
        cmbCategoria.setPromptText("Seleccionar categoría");
        txtPrecio.clear();
        txtStock.clear();
        imagenSeleccionada = "";
        lblRutaImagen.setText("Sin imagen seleccionada");
        lblRutaImagen.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}