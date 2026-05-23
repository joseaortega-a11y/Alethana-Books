package com.alethanabooks.controlador;

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

public class MasVendidosController implements Initializable {

    @FXML private VBox listaLibros;

    private final CatalogoService catalogoService = new CatalogoService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarLibros();
    }

    private void cargarLibros() {
        listaLibros.getChildren().clear();
        List<Libro> libros = catalogoService.obtenerTodos();
        String[] colores = {"#f59e0b", "#94a3b8", "#b45309", "#10b981", "#7c3aed",
                "#ec4899", "#3b82f6", "#0ea574", "#ef4444", "#6366f1"};

        if (libros.isEmpty()) {
            Label lbl = new Label("No hay libros en el catálogo aún.");
            lbl.setStyle("-fx-font-size: 15px;");
            lbl.setTextFill(Color.web("#94a3b8"));
            listaLibros.getChildren().add(lbl);
            return;
        }

        for (int i = 0; i < libros.size(); i++) {
            listaLibros.getChildren().add(
                    crearFilaLibro(libros.get(i), i + 1, colores[Math.min(i, colores.length - 1)]));
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

        StackPane imgPane = ImagenUtil.crearPanelImagen(libro.getImagen(), 95, 140);

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
}