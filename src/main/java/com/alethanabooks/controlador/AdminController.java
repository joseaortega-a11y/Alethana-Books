package com.alethanabooks.controlador;

import com.alethanabooks.factory.LibroFactory;
import com.alethanabooks.functional.FiltroLibro;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.persistence.RutasDatos;
import com.alethanabooks.service.CatalogoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    @FXML
    private TableView<Libro> tablaLibros;
    @FXML
    private TableColumn<Libro, String> colId;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutor;
    @FXML
    private TableColumn<Libro, String> colCategoria;
    @FXML
    private TableColumn<Libro, Double> colPrecio;
    @FXML
    private TableColumn<Libro, Integer> colStock;
    @FXML
    private TableColumn<Libro, String> colImagen;

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private ComboBox<String> cmbTipo;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private Label lblRutaImagen;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnModificar;

    @FXML
    private TextField txtBuscar;

    private final CatalogoService catalogoService = new CatalogoService();
    private ObservableList<Libro> librosObservable;
    private String imagenSeleccionada = "";
    private Libro libroEnEdicion = null; // null = modo agregar, not null = modo editar

    public static final List<String> CATEGORIAS = List.of(
            "Artes", "Biografias y literatura", "Ciencia", "Tecnologia",
            "Negocios y finanzas", "Ficcion", "Filosofia", "Historia", "Literatura juvenil"
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

        cmbTipo.setItems(FXCollections.observableArrayList("Nacional", "Importado"));
        cmbTipo.setValue("Nacional");

        cargarLibros();

        // Lambda: filtrar en tiempo real
        txtBuscar.textProperty().addListener((obs, old, texto) -> {
            FiltroLibro filtro = libro -> libro.coincideCon(texto);
            List<Libro> filtrados = catalogoService.obtenerTodos().stream()
                    .filter(filtro::filtrar).toList();
            tablaLibros.setItems(FXCollections.observableArrayList(filtrados));
        });

        // Lambda: al seleccionar fila, cargar datos en el formulario para editar
        tablaLibros.getSelectionModel().selectedItemProperty().addListener((obs, old, seleccionado) -> {
            if (seleccionado != null) {
                cargarEnFormulario(seleccionado);
            }
        });

        // Lambda: botones dependientes de selección
        btnEliminar.disableProperty().bind(
                tablaLibros.getSelectionModel().selectedItemProperty().isNull()
        );
        btnModificar.disableProperty().bind(
                tablaLibros.getSelectionModel().selectedItemProperty().isNull()
        );
    }

    private void cargarEnFormulario(Libro libro) {
        libroEnEdicion = libro;
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        cmbCategoria.setValue(libro.getCategoria());
        txtPrecio.setText(String.valueOf((int) libro.getPrecio()));
        txtStock.setText(String.valueOf(libro.getStock()));
        imagenSeleccionada = libro.getImagen() != null ? libro.getImagen() : "";
        lblRutaImagen.setText(imagenSeleccionada.isBlank() ? "Sin imagen" : imagenSeleccionada);
        lblRutaImagen.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                (imagenSeleccionada.isBlank() ? "#94a3b8" : "#10b981") + ";");
        btnAgregar.setText("Cancelar edición");
    }

    @FXML
    private void onSeleccionarImagen() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar portada del libro");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.webp"));

        File archivo = fc.showOpenDialog(btnAgregar.getScene().getWindow());
        if (archivo == null) return;

        try {
            // Copiar a resources/imagenes para que el classpath la encuentre
            Path destino = Path.of(RutasDatos.CARPETA_IMAGENES + archivo.getName());
            Files.createDirectories(destino.getParent());
            Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            imagenSeleccionada = archivo.getName();
            lblRutaImagen.setText(archivo.getName());
            lblRutaImagen.setStyle("-fx-font-size: 12px; -fx-text-fill: #10b981;");
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo copiar la imagen: " + e.getMessage());
        }
    }

    @FXML
    private void onVolverInicio() {
        navegar("/fxml/Login.fxml", "Alethana Books - Login", 492, 572);
    }

    @FXML
    private void onAgregar() {
        // Si está en modo edición, este botón cancela
        if (libroEnEdicion != null) {
            libroEnEdicion = null;
            limpiarCampos();
            btnAgregar.setText("+ Agregar");
            return;
        }

        try {
            String titulo = txtTitulo.getText().trim();
            String autor = txtAutor.getText().trim();
            String categoria = cmbCategoria.getValue();
            String tipo = cmbTipo.getValue();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (titulo.isEmpty() || autor.isEmpty() || categoria == null || tipo == null) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.");
                return;
            }

            LibroFactory.TipoLibro tipoLibro = "Importado".equals(tipo)
                    ? LibroFactory.TipoLibro.DIGITAL
                    : LibroFactory.TipoLibro.FISICO;

            Libro nuevo = LibroFactory.crearLibro(
                    tipoLibro, UUID.randomUUID().toString(),
                    titulo, autor, categoria, precio, stock, imagenSeleccionada);

            catalogoService.agregar(nuevo);
            cargarLibros();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio y stock deben ser números válidos.");
        }
    }

    @FXML
    private void onModificar() {
        Libro seleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        try {
            String titulo = txtTitulo.getText().trim();
            String autor = txtAutor.getText().trim();
            String categoria = cmbCategoria.getValue();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (titulo.isEmpty() || autor.isEmpty() || categoria == null) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.");
                return;
            }

            // Actualizar el objeto seleccionado
            seleccionado.setTitulo(titulo);
            seleccionado.setAutor(autor);
            seleccionado.setCategoria(categoria);
            seleccionado.setPrecio(precio);
            seleccionado.setStock(stock);
            if (!imagenSeleccionada.isBlank()) {
                seleccionado.setImagen(imagenSeleccionada);
            }

            catalogoService.actualizar(seleccionado);
            cargarLibros();
            limpiarCampos();
            libroEnEdicion = null;
            btnAgregar.setText("+ Agregar");

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
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                catalogoService.eliminar(seleccionado.getId());
                cargarLibros();
                limpiarCampos();
                libroEnEdicion = null;
                btnAgregar.setText("+ Agregar");
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
        cmbTipo.setValue("Nacional");
        txtPrecio.clear();
        txtStock.clear();
        imagenSeleccionada = "";
        lblRutaImagen.setText("Sin imagen seleccionada");
        lblRutaImagen.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
        tablaLibros.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void navegar(String fxml, String titulo, double ancho, double alto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root, ancho, alto));
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

