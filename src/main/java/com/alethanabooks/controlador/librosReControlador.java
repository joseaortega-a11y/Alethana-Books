package com.alethanabooks.controlador;

import com.alethanabooks.modelo.Libro;
import com.alethanabooks.service.CatalogoService;
import com.alethanabooks.Util.ImagenUtil;
import com.alethanabooks.Util.TarjetaUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import com.alethanabooks.modelo.SesionActual;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class librosReControlador implements Initializable {

    @FXML
    private FlowPane flowLibros;

    private final CatalogoService catalogoService = new CatalogoService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        generarRecomendacion();
    }

    @FXML
    private void onGenerarRecomendacion() {
        generarRecomendacion();
    }

    private void generarRecomendacion() {
        flowLibros.getChildren().clear();
        List<Libro> recomendados = catalogoService.obtenerRecomendadosAleatorios(12);

        if (recomendados.isEmpty()) {
            Label lbl = new Label("No hay libros disponibles aún. El administrador debe agregar libros primero.");
            lbl.setStyle("-fx-font-size: 15px;");
            lbl.setTextFill(Color.web("#94a3b8"));
            lbl.setWrapText(true);
            flowLibros.getChildren().add(lbl);
            return;
        }

        for (Libro libro : recomendados) {
            flowLibros.getChildren().add(crearTarjetaLibro(libro));
        }
    }

    private VBox crearTarjetaLibro(Libro libro) {
        VBox card = new VBox(10);
        TarjetaUtil.anchoAdaptable(card, flowLibros, 5, 200);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 14;");
        card.setPadding(new Insets(18));

        StackPane imgPane = ImagenUtil.crearPanelImagen(libro.getImagen(), 164, 220);

        Label lblTitulo = new Label(libro.getTitulo());
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: 800;");
        lblTitulo.setTextFill(Color.web("#0f172a"));
        TarjetaUtil.tituloUnaLinea(lblTitulo);

        Label lblAutor = new Label(libro.getAutor());
        lblAutor.setStyle("-fx-font-size: 13px;");
        lblAutor.setTextFill(Color.web("#64748b"));
        TarjetaUtil.unaLinea(lblAutor);

        Label lblCategoria = new Label(libro.getCategoria());
        lblCategoria.setStyle("-fx-background-color: #ede9fe; -fx-background-radius: 6; " +
                "-fx-font-size: 11px; -fx-font-weight: 700; -fx-padding: 3 8;");
        lblCategoria.setTextFill(Color.web("#7c3aed"));

        Label lblPrecio = new Label(String.format("COP %,.0f", libro.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 20px; -fx-font-weight: 900;");
        lblPrecio.setTextFill(Color.web("#7c3aed"));

        boolean hayStock = libro.getStock() > 0;
        Label lblStock = new Label(hayStock ? "Stock: " + libro.getStock() : "Sin stock");
        lblStock.setStyle("-fx-font-size: 12px; -fx-font-weight: 700;");
        lblStock.setTextFill(hayStock ? Color.web("#10b981") : Color.web("#ef4444"));


        Button btnCarrito = new Button(hayStock ? "Agregar al carrito" : "Sin stock");
        btnCarrito.setMaxWidth(Double.MAX_VALUE);
        btnCarrito.setTextFill(Color.WHITE);
        btnCarrito.setDisable(!hayStock);
        btnCarrito.setStyle("-fx-background-color: " + (hayStock ? TarjetaUtil.COLOR_BOTON : "#94a3b8") +
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
}
