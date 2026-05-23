package com.alethanabooks.controlador;

import com.alethanabooks.modelo.Libro;
import com.alethanabooks.persistence.RutasDatos;
import com.alethanabooks.service.CatalogoService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import com.alethanabooks.util.ImagenUtil;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class InicioController implements Initializable {

    @FXML private FlowPane gridLibros;
    @FXML private VBox sidebarCategorias;
    @FXML private HBox bannerPortadas;
    @FXML private VBox sidebarNovedad;

    private final CatalogoService catalogoService = new CatalogoService();
    private String categoriaActiva = null;

    private static final String[] COLORES_BTN   = {"#7c3aed", "#ec4899", "#10b981", "#f59e0b", "#0ea574"};
    private static final String[] COLORES_BANNER = {"#1e293b", "#33fdcb", "#be185d", "#7c3aed", "#0ea574"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        construirSidebar();
        cargarLibros(null);
        actualizarBanner();
        actualizarNovedad();
    }

    private void actualizarBanner() {
        bannerPortadas.getChildren().clear();
        List<Libro> libros = catalogoService.obtenerTodos();

        if (libros.isEmpty()) {
            Label vacio = new Label("📚\nAún no hay libros");
            vacio.setStyle("-fx-font-size: 18px; -fx-text-alignment: center;");
            vacio.setTextFill(Color.WHITE);
            bannerPortadas.getChildren().add(vacio);
            return;
        }

        int cantidad = Math.min(3, libros.size());
        for (int i = 0; i < cantidad; i++) {
            String color = COLORES_BANNER[i % COLORES_BANNER.length];
            bannerPortadas.getChildren().add(crearPortadaBanner(libros.get(i), color));
        }
    }

    private void actualizarNovedad() {
        sidebarNovedad.getChildren().clear();
        List<Libro> libros = catalogoService.obtenerTodos();

        if (libros.isEmpty()) {
            Label vacio = new Label("Aún no hay novedades.\nAgrega un libro para verlo aquí.");
            vacio.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");
            vacio.setTextFill(Color.web("#94a3b8"));
            vacio.setWrapText(true);
            sidebarNovedad.getChildren().add(vacio);
            return;
        }

        Libro novedad = libros.get(libros.size() - 1);

        StackPane imgPane = new StackPane();
        imgPane.setPrefHeight(230);
        imgPane.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;");
        Label emoji = new Label("📚");
        emoji.setStyle("-fx-font-size: 64px;");
        imgPane.getChildren().add(emoji);

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

        Button btnDetalle = new Button("Ver detalles");
        btnDetalle.setMaxWidth(Double.MAX_VALUE);
        btnDetalle.setTextFill(Color.WHITE);
        btnDetalle.setStyle("-fx-background-color: linear-gradient(to right, #ec4899, #7c3aed); " +
                "-fx-background-radius: 10; -fx-font-size: 16px; -fx-font-weight: 900; -fx-padding: 13;");

        sidebarNovedad.getChildren().addAll(imgPane, lblTitulo, lblAutor, lblPrecio, lblCategoria, btnDetalle);
    }

    private void construirSidebar() {
        Button btnTodas = new Button("Todas las categorías");
        btnTodas.setMaxWidth(Double.MAX_VALUE);
        btnTodas.setAlignment(Pos.CENTER_LEFT);
        btnTodas.setStyle(estiloCategoria(true));
        btnTodas.setOnAction(e -> {
            categoriaActiva = null;
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
            btnCat.setOnAction(e -> seleccionarCategoria(btnCat, cat));
            sidebarCategorias.getChildren().add(btnCat);
        }
    }

    private void seleccionarCategoria(Button clickado, String categoria) {
        categoriaActiva = categoria;
        sidebarCategorias.getChildren().forEach(n -> {
            if (n instanceof Button b) b.setStyle(estiloCategoria(false));
        });
        clickado.setStyle(estiloCategoria(true));
        cargarLibros(categoria);
    }

    private void cargarLibros(String categoria) {
        gridLibros.getChildren().clear();
        List<Libro> libros = (categoria == null)
                ? catalogoService.obtenerTodos()
                : catalogoService.filtrarPorCategoria(categoria);

        if (libros.isEmpty()) {
            Label lbl = new Label(categoria == null
                    ? "El catálogo está vacío."
                    : "No hay libros en la categoría \"" + categoria + "\".");
            lbl.setStyle("-fx-font-size: 15px;");
            lbl.setTextFill(Color.web("#94a3b8"));
            lbl.setWrapText(true);
            gridLibros.getChildren().add(lbl);
            return;
        }

        for (int i = 0; i < libros.size(); i++) {
            String colorBtn = COLORES_BTN[i % COLORES_BTN.length];
            gridLibros.getChildren().add(crearTarjeta(libros.get(i), colorBtn));
        }
    }
    private StackPane crearPortadaBanner(Libro libro, String colorFondo) {
        StackPane pane = new StackPane();
        pane.setPrefSize(130, 210);
        pane.setStyle("-fx-background-color: " + colorFondo + "; -fx-background-radius: 10;");

        // Imagen si existe, sino emoji
        String img = libro.getImagen();
        if (img != null && !img.isBlank()) {
            File archivo = new File(RutasDatos.CARPETA_IMAGENES + img);
            if (archivo.exists()) {
                ImageView iv = new ImageView(new Image(archivo.toURI().toString()));
                iv.setFitWidth(122);
                iv.setFitHeight(210);
                iv.setPreserveRatio(true);
                pane.getChildren().add(iv);
            }
        } else {
            Label emoji = new Label("📚");
            emoji.setStyle("-fx-font-size: 48px;");
            pane.getChildren().add(emoji);
        }

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

        // Usar ImagenUtil para manejar imagen o fallback
        StackPane imgPane = ImagenUtil.crearPanelImagen(libro.getImagen(), 154, 210);

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
        btnCarrito.setStyle("-fx-background-color: " + colorBtn + "; -fx-background-radius: 10; " +
                "-fx-font-size: 13px; -fx-font-weight: 800;");

        card.getChildren().addAll(imgPane, lblTitulo, lblAutor, lblCategoria, lblPrecio, btnCarrito);
        return card;
    }


    private String estiloCategoria(boolean activo) {
        if (activo) {
            return "-fx-background-color: #ede9fe; -fx-background-radius: 8; " +
                    "-fx-font-size: 15px; -fx-font-weight: 800; " +
                    "-fx-text-fill: #7c3aed; -fx-border-color: transparent; -fx-padding: 6 10;";
        }
        return "-fx-background-color: transparent; -fx-background-radius: 8; " +
                "-fx-font-size: 15px; -fx-font-weight: 400; " +
                "-fx-text-fill: #334155; -fx-border-color: transparent; -fx-padding: 6 10;";
    }
}