package com.alethanabooks.controlador;

import com.alethanabooks.modelo.SesionActual;
import com.alethanabooks.interfaces.Descargable;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.service.CatalogoService;
import com.alethanabooks.Util.ImagenUtil;
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

public class InicioController implements Initializable {

    @FXML private FlowPane gridLibros;
    @FXML private VBox     sidebarCategorias;
    @FXML private HBox     bannerPortadas;
    @FXML private VBox     sidebarNovedad;

    private final CatalogoService catalogoService = new CatalogoService();

    private static final String[] COLORES_BTN    = {"#7c3aed","#ec4899","#10b981","#f59e0b","#0ea574"};
    private static final String[] COLORES_BANNER = {"#1e293b","#33fdcb","#be185d","#7c3aed","#0ea574"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        construirSidebar();
        cargarLibros(null);
        actualizarBanner();
        actualizarNovedad();
    }

    // Llamado desde HeaderController al buscar
    public void buscarDesdeHeader(String texto) {
        gridLibros.getChildren().clear();
        List<Libro> libros = texto.isEmpty()
                ? catalogoService.obtenerTodos()
                : catalogoService.buscar(texto);
        poblarGrid(libros);
    }

    private void actualizarBanner() {
        bannerPortadas.getChildren().clear();
        List<Libro> libros = catalogoService.obtenerTodos();
        if (libros.isEmpty()) return;
        int cantidad = Math.min(3, libros.size());
        for (int i = 0; i < cantidad; i++) {
            bannerPortadas.getChildren().add(crearPortadaBanner(libros.get(i), COLORES_BANNER[i]));
        }
    }

    private void actualizarNovedad() {
        sidebarNovedad.getChildren().clear();
        List<Libro> libros = catalogoService.obtenerTodos();

        if (libros.isEmpty()) {
            Label v = new Label("Aún no hay novedades.");
            v.setStyle("-fx-font-size: 14px;");
            v.setTextFill(Color.web("#94a3b8"));
            sidebarNovedad.getChildren().add(v);
            return;
        }

        Libro novedad = libros.get(libros.size() - 1);
        StackPane imgPane = ImagenUtil.crearPanelImagen(novedad.getImagen(), 200, 230);

        Label lblTitulo = new Label(novedad.getTitulo());
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: 900;");
        lblTitulo.setTextFill(Color.web("#0f172a"));
        lblTitulo.setWrapText(true);

        Label lblAutor = new Label(novedad.getAutor());
        lblAutor.setStyle("-fx-font-size: 16px;");
        lblAutor.setTextFill(Color.web("#64748b"));

        Label lblPrecio = new Label(String.format("COP %,.0f", novedad.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 23px; -fx-font-weight: 900;");
        lblPrecio.setTextFill(Color.web("#7c3aed"));

        Label lblCategoria = new Label(novedad.getCategoria());
        lblCategoria.setStyle("-fx-background-color: #f59e0b; -fx-background-radius: 14; " +
                "-fx-font-size: 13px; -fx-font-weight: 800; -fx-padding: 7 18;");
        lblCategoria.setTextFill(Color.WHITE);

        // Stock novedad
        Label lblStock = new Label("Stock: " + novedad.getStock());
        lblStock.setStyle("-fx-font-size: 13px; -fx-font-weight: 700;");
        lblStock.setTextFill(novedad.getStock() > 0
                ? Color.web("#10b981") : Color.web("#ef4444"));

        Button btnAgregar = new Button(novedad.getStock() > 0 ? "Agregar al carrito" : "Sin stock");
        btnAgregar.setMaxWidth(Double.MAX_VALUE);
        btnAgregar.setTextFill(Color.WHITE);
        btnAgregar.setDisable(novedad.getStock() == 0);
        btnAgregar.setStyle("-fx-background-color: linear-gradient(to right, #ec4899, #7c3aed); " +
                "-fx-background-radius: 10; -fx-font-size: 16px; -fx-font-weight: 900; -fx-padding: 13;");
        btnAgregar.setOnAction(e -> agregarAlCarrito(novedad));

        sidebarNovedad.getChildren().addAll(imgPane, lblTitulo, lblAutor,
                lblPrecio, lblCategoria, lblStock, btnAgregar);
    }

    private void construirSidebar() {
        Button btnTodas = new Button("Todas las categorías");
        btnTodas.setMaxWidth(Double.MAX_VALUE);
        btnTodas.setAlignment(Pos.CENTER_LEFT);
        btnTodas.setStyle(estiloCategoria(true));
        btnTodas.setOnAction(e -> {
            sidebarCategorias.getChildren().forEach(n -> {
                if (n instanceof Button b) b.setStyle(estiloCategoria(false));
            });
            btnTodas.setStyle(estiloCategoria(true));
            cargarLibros(null);
        });
        sidebarCategorias.getChildren().add(btnTodas);

        for (String cat : AdminController.CATEGORIAS) {
            Button btnCat = new Button(cat);
            btnCat.setMaxWidth(Double.MAX_VALUE);
            btnCat.setAlignment(Pos.CENTER_LEFT);
            btnCat.setStyle(estiloCategoria(false));
            btnCat.setOnAction(e -> {
                sidebarCategorias.getChildren().forEach(n -> {
                    if (n instanceof Button b) b.setStyle(estiloCategoria(false));
                });
                btnCat.setStyle(estiloCategoria(true));
                cargarLibros(cat);
            });
            sidebarCategorias.getChildren().add(btnCat);
        }
    }

    private void cargarLibros(String categoria) {
        gridLibros.getChildren().clear();
        List<Libro> libros = (categoria == null)
                ? catalogoService.obtenerTodos()
                : catalogoService.filtrarPorCategoria(categoria);
        poblarGrid(libros);
    }

    private void poblarGrid(List<Libro> libros) {
        if (libros.isEmpty()) {
            Label lbl = new Label("No hay libros disponibles.");
            lbl.setStyle("-fx-font-size: 15px;");
            lbl.setTextFill(Color.web("#94a3b8"));
            gridLibros.getChildren().add(lbl);
            return;
        }
        for (int i = 0; i < libros.size(); i++) {
            gridLibros.getChildren().add(crearTarjeta(libros.get(i), COLORES_BTN[i % COLORES_BTN.length]));
        }
    }

    private StackPane crearPortadaBanner(Libro libro, String colorFondo) {
        StackPane pane = new StackPane();
        pane.setPrefSize(130, 210);
        pane.setStyle("-fx-background-color: " + colorFondo + "; -fx-background-radius: 10;");
        StackPane imgPane = ImagenUtil.crearPanelImagen(libro.getImagen(), 122, 210);
        imgPane.setStyle("");
        pane.getChildren().add(imgPane);
        Label titulo = new Label(libro.getTitulo());
        titulo.setStyle("-fx-font-size: 11px; -fx-font-weight: 800; -fx-text-alignment: center;");
        titulo.setTextFill(Color.WHITE);
        titulo.setWrapText(true);
        titulo.setMaxWidth(110);
        titulo.setPadding(new Insets(0, 4, 8, 4));
        StackPane.setAlignment(titulo, Pos.BOTTOM_CENTER);
        pane.getChildren().add(titulo);
        return pane;
    }

    private VBox crearTarjeta(Libro libro, String colorBtn) {
        VBox card = new VBox(10);
        card.setPrefWidth(190);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 14;");
        card.setPadding(new Insets(18));

        StackPane imgPane = ImagenUtil.crearPanelImagen(libro.getImagen(), 154, 210);

        // Badge descargable si implementa la interfaz
        if (libro instanceof Descargable d && d.estaDisponibleParaDescarga()) {
            Label badge = new Label("⬇ Digital");
            badge.setStyle("-fx-background-color: #0ea574; -fx-background-radius: 6; " +
                    "-fx-font-size: 10px; -fx-font-weight: 800; -fx-padding: 3 8;");
            badge.setTextFill(Color.WHITE);
            StackPane.setAlignment(badge, Pos.TOP_LEFT);
            imgPane.getChildren().add(badge);
        }

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

        // Stock
        boolean hayStock = libro.getStock() > 0;
        Label lblStock = new Label(hayStock ? "Stock: " + libro.getStock() : "Sin stock");
        lblStock.setStyle("-fx-font-size: 12px; -fx-font-weight: 700;");
        lblStock.setTextFill(hayStock ? Color.web("#10b981") : Color.web("#ef4444"));

        Button btnCarrito = new Button(hayStock ? "Agregar al carrito" : "Sin stock");
        btnCarrito.setMaxWidth(Double.MAX_VALUE);
        btnCarrito.setTextFill(Color.WHITE);
        btnCarrito.setDisable(!hayStock);
        btnCarrito.setStyle("-fx-background-color: " + (hayStock ? colorBtn : "#94a3b8") +
                "; -fx-background-radius: 10; -fx-font-size: 13px; -fx-font-weight: 800;");
        btnCarrito.setOnAction(e -> agregarAlCarrito(libro));

        card.getChildren().addAll(imgPane, lblTitulo, lblAutor, lblCategoria, lblPrecio, lblStock, btnCarrito);
        return card;
    }

    private void agregarAlCarrito(Libro libro) {
        if (!SesionActual.haySesion()) {
            new Alert(Alert.AlertType.INFORMATION, "Inicia sesión para agregar al carrito.").showAndWait();
            return;
        }
        try {
            SesionActual.getCarritoService().agregarLibro(libro, 1);
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Carrito");
            ok.setHeaderText(null);
            ok.setContentText("\"" + libro.getTitulo() + "\" agregado al carrito.");
            ok.showAndWait();
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        }
    }

    private String estiloCategoria(boolean activo) {
        return activo
                ? "-fx-background-color: #ede9fe; -fx-background-radius: 8; -fx-font-size: 15px; " +
                "-fx-font-weight: 800; -fx-text-fill: #7c3aed; -fx-border-color: transparent; -fx-padding: 6 10;"
                : "-fx-background-color: transparent; -fx-background-radius: 8; -fx-font-size: 15px; " +
                "-fx-font-weight: 400; -fx-text-fill: #334155; -fx-border-color: transparent; -fx-padding: 6 10;";
    }
}