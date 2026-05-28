package com.alethanabooks.controlador;

import com.alethanabooks.factory.LibroFactory;
import com.alethanabooks.functional.FiltroLibro;
import com.alethanabooks.interfaces.Validable;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.modelo.Venta;
import com.alethanabooks.persistence.RutasDatos;
import com.alethanabooks.persistence.VentaRepository;
import com.alethanabooks.service.CatalogoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class AdminController implements Initializable {

    // ── Tabla ──────────────────────────────────────────────────────────────
    @FXML private TableView<Libro>           tablaLibros;
    @FXML private TableColumn<Libro,String>  colId, colTitulo, colAutor, colCategoria, colImagen;
    @FXML private TableColumn<Libro,Double>  colPrecio;
    @FXML private TableColumn<Libro,Integer> colStock;

    // ── Formulario ────────────────────────────────────────────────────────
    @FXML private TextField      txtTitulo, txtAutor, txtPrecio, txtStock, txtBuscar;
    @FXML private ComboBox<String> cmbCategoria, cmbTipo, cmbFormato;
    @FXML private Label          lblRutaImagen, lblRutaArchivo;
    @FXML private Button         btnAgregar, btnEliminar, btnModificar, btnArchivo;

    // ── Pedidos ───────────────────────────────────────────────────────────
    @FXML private VBox           listaPedidos;

    private final CatalogoService  catalogoService  = new CatalogoService();
    private final VentaRepository  ventaRepository  = new VentaRepository();
    private ObservableList<Libro>  librosObservable;
    private String imagenSeleccionada  = "";
    private String archivoDescargable  = "";
    private Libro  libroEnEdicion      = null;

    public static final List<String> CATEGORIAS = List.of(
            "Artes","Biografias y literatura","Ciencia","Tecnologia",
            "Negocios y finanzas","Ficcion","Filosofia","Historia","Literatura juvenil"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colImagen.setCellValueFactory(new PropertyValueFactory<>("imagen"));

        // ComboBox
        cmbCategoria.setItems(FXCollections.observableArrayList(CATEGORIAS));
        cmbCategoria.setPromptText("Seleccionar categoría");
        cmbTipo.setItems(FXCollections.observableArrayList("Nacional", "Importado"));
        cmbTipo.setValue("Nacional");
        cmbFormato.setItems(FXCollections.observableArrayList("PDF", "EPUB", "MOBI"));
        cmbFormato.setValue("PDF");

        // Mostrar/ocultar campos de digital según tipo
        cmbTipo.valueProperty().addListener((obs, old, tipo) -> {
            boolean esDigital = "Importado".equals(tipo);
            cmbFormato.setVisible(esDigital);
            cmbFormato.setManaged(esDigital);
            btnArchivo.setVisible(esDigital);
            btnArchivo.setManaged(esDigital);
            lblRutaArchivo.setVisible(esDigital);
            lblRutaArchivo.setManaged(esDigital);
        });
        // Estado inicial: Nacional, campos digitales ocultos
        cmbFormato.setVisible(false); cmbFormato.setManaged(false);
        btnArchivo.setVisible(false);  btnArchivo.setManaged(false);
        lblRutaArchivo.setVisible(false); lblRutaArchivo.setManaged(false);

        cargarLibros();
        cargarPedidos();

        // Búsqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, old, texto) -> {
            FiltroLibro filtro = libro -> libro.coincideCon(texto);
            tablaLibros.setItems(FXCollections.observableArrayList(
                    catalogoService.obtenerTodos().stream().filter(filtro::filtrar).toList()));
        });

        // Selección fila → carga formulario
        tablaLibros.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) cargarEnFormulario(sel);
        });

        btnEliminar.disableProperty().bind(tablaLibros.getSelectionModel().selectedItemProperty().isNull());
        btnModificar.disableProperty().bind(tablaLibros.getSelectionModel().selectedItemProperty().isNull());
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
        btnAgregar.setText("Cancelar edición");
    }

    @FXML
    private void onSeleccionarImagen() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar portada");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png","*.jpg","*.jpeg","*.webp"));
        File archivo = fc.showOpenDialog(btnAgregar.getScene().getWindow());
        if (archivo == null) return;
        try {
            Path dest = Path.of(RutasDatos.CARPETA_IMAGENES + archivo.getName());
            Files.createDirectories(dest.getParent());
            Files.copy(archivo.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            imagenSeleccionada = archivo.getName();
            lblRutaImagen.setText(archivo.getName());
            lblRutaImagen.setStyle("-fx-font-size: 12px; -fx-text-fill: #10b981;");
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo copiar la imagen: " + e.getMessage());
        }
    }

    @FXML
    private void onSeleccionarArchivo() {
        String formato = cmbFormato.getValue() != null ? cmbFormato.getValue() : "PDF";
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar archivo descargable");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Archivos digitales", "*.pdf","*.epub","*.mobi"));
        File archivo = fc.showOpenDialog(btnAgregar.getScene().getWindow());
        if (archivo == null) return;
        try {
            Path dest = Path.of(RutasDatos.CARPETA_DESCARGABLES + archivo.getName());
            Files.createDirectories(dest.getParent());
            Files.copy(archivo.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            archivoDescargable = dest.toString();
            lblRutaArchivo.setText(archivo.getName());
            lblRutaArchivo.setStyle("-fx-font-size: 12px; -fx-text-fill: #10b981;");
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo copiar el archivo: " + e.getMessage());
        }
    }

    @FXML
    private void onAgregar() {
        if (libroEnEdicion != null) {
            libroEnEdicion = null;
            limpiarCampos();
            btnAgregar.setText("+ Agregar");
            return;
        }
        try {
            String titulo    = txtTitulo.getText().trim();
            String autor     = txtAutor.getText().trim();
            String categoria = cmbCategoria.getValue();
            String tipo      = cmbTipo.getValue();
            String formato   = cmbFormato.getValue();
            double precio    = Double.parseDouble(txtPrecio.getText().trim());
            int    stock     = Integer.parseInt(txtStock.getText().trim());

            if (titulo.isEmpty() || autor.isEmpty() || categoria == null || tipo == null) {
                mostrarAlerta("Error", "Título, autor, categoría y tipo son obligatorios.");
                return;
            }
            if (precio <= 0) { mostrarAlerta("Error", "El precio debe ser mayor a cero."); return; }
            if (stock < 0)   { mostrarAlerta("Error", "El stock no puede ser negativo."); return; }

            LibroFactory.TipoLibro tipoLibro = "Importado".equals(tipo)
                    ? LibroFactory.TipoLibro.DIGITAL : LibroFactory.TipoLibro.FISICO;

            Libro nuevo = LibroFactory.crearLibro(tipoLibro, UUID.randomUUID().toString(),
                    titulo, autor, categoria, precio, stock, imagenSeleccionada,
                    formato, archivoDescargable);

            // Validar si implementa Validable
            if (nuevo instanceof Validable v && !v.esValido()) {
                mostrarAlerta("Libro inválido", v.obtenerMensajeError());
                return;
            }

            catalogoService.agregar(nuevo);
            cargarLibros();
            limpiarCampos();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Precio y stock deben ser números válidos.\n" +
                    "Precio: usa punto decimal (ej: 29000.0)\nStock: solo números enteros.");
        }
    }

    @FXML
    private void onModificar() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            String titulo    = txtTitulo.getText().trim();
            String autor     = txtAutor.getText().trim();
            String categoria = cmbCategoria.getValue();
            double precio    = Double.parseDouble(txtPrecio.getText().trim());
            int    stock     = Integer.parseInt(txtStock.getText().trim());

            if (titulo.isEmpty() || autor.isEmpty() || categoria == null) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.");
                return;
            }
            if (precio <= 0) { mostrarAlerta("Error", "El precio debe ser mayor a cero."); return; }
            if (stock < 0)   { mostrarAlerta("Error", "El stock no puede ser negativo."); return; }

            sel.setTitulo(titulo);
            sel.setAutor(autor);
            sel.setCategoria(categoria);
            sel.setPrecio(precio);
            sel.setStock(stock);
            if (!imagenSeleccionada.isBlank()) sel.setImagen(imagenSeleccionada);

            catalogoService.actualizar(sel);
            cargarLibros();
            limpiarCampos();
            libroEnEdicion = null;
            btnAgregar.setText("+ Agregar");
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Precio y stock deben ser números válidos.");
        }
    }

    @FXML
    private void onEliminar() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar \"" + sel.getTitulo() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                catalogoService.eliminar(sel.getId());
                cargarLibros();
                limpiarCampos();
                libroEnEdicion = null;
                btnAgregar.setText("+ Agregar");
            }
        });
    }

    private void cargarPedidos() {
        if (listaPedidos == null) return;
        listaPedidos.getChildren().clear();
        List<Venta> ventas = ventaRepository.obtenerTodas();
        if (ventas.isEmpty()) {
            Label lbl = new Label("No hay pedidos registrados aún.");
            lbl.setStyle("-fx-font-size: 14px;");
            lbl.setTextFill(Color.web("#94a3b8"));
            listaPedidos.getChildren().add(lbl);
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (int i = ventas.size() - 1; i >= 0; i--) {
            Venta v = ventas.get(i);
            VBox card = new VBox(6);
            card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; " +
                    "-fx-border-color: #e5e7eb; -fx-border-radius: 10;");
            card.setPadding(new Insets(12, 16, 12, 16));

            String usuario = v.getUsuario() != null ? v.getUsuario().getNombre() +
                    " (" + v.getUsuario().getCorreo() + ")" : "Desconocido";
            String fecha = v.getFecha() != null ? v.getFecha().format(fmt) : "—";

            Label lblInfo = new Label("👤 " + usuario + "   📅 " + fecha +
                    "   💳 " + v.getMetodoPago());
            lblInfo.setStyle("-fx-font-size: 12px;");
            lblInfo.setTextFill(Color.web("#64748b"));

            v.getDetalles().forEach(d -> {
                Label l = new Label("  • " + d.getLibro().getTitulo() +
                        " x" + d.getCantidad() +
                        " → COP " + String.format("%,.0f", d.getSubtotal()));
                l.setStyle("-fx-font-size: 13px;");
                l.setTextFill(Color.web("#0f172a"));
                card.getChildren().add(l);
            });

            Label lblTotal = new Label("Total: COP " + String.format("%,.0f", v.getTotal()));
            lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: 900;");
            lblTotal.setTextFill(Color.web("#7c3aed"));
            card.getChildren().addAll(lblInfo, lblTotal);
            listaPedidos.getChildren().add(card);
        }
    }

    private void cargarLibros() {
        librosObservable = FXCollections.observableArrayList(catalogoService.obtenerTodos());
        tablaLibros.setItems(librosObservable);
    }

    private void limpiarCampos() {
        txtTitulo.clear(); txtAutor.clear(); txtPrecio.clear(); txtStock.clear();
        cmbCategoria.setValue(null); cmbCategoria.setPromptText("Seleccionar categoría");
        cmbTipo.setValue("Nacional"); cmbFormato.setValue("PDF");
        imagenSeleccionada = ""; archivoDescargable = "";
        lblRutaImagen.setText("Sin imagen seleccionada");
        lblRutaImagen.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
        lblRutaArchivo.setText("Sin archivo seleccionado");
        lblRutaArchivo.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
        tablaLibros.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(mensaje);
        a.showAndWait();
    }

    @FXML
    private void onVolverInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Alethana Books - Login");
            stage.setScene(new Scene(root, 492, 572));
            stage.setResizable(false);
            stage.show();
            ((Stage) btnAgregar.getScene().getWindow()).close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}