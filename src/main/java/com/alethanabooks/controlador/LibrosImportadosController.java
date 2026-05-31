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
import com.alethanabooks.modelo.SesionActual;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LibrosImportadosController implements Initializable {

    @FXML
    private FlowPane flowLibros;

    private final CatalogoService catalogoService = new CatalogoService();
    private static final String[] COLORES = {"#7c3aed", "#ec4899", "#10b981", "#f59e0b", "#3b82f6", "#0ea574"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarLibros();
    }

    private void cargarLibros() {
        flowLibros.getChildren().clear();

        List<com.alethanabooks.modelo.LibroFisico> importados = catalogoService.obtenerTodos().stream()
                .filter(l -> l instanceof com.alethanabooks.modelo.LibroFisico lf
                        && "Importado".equals(lf.getOrigen()))
                .map(l -> (com.alethanabooks.modelo.LibroFisico) l)
                .toList();

        if (importados.isEmpty()) {
            Label lbl = new Label("No hay libros importados disponibles aún.");
            lbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #94a3b8;");
            flowLibros.getChildren().add(lbl);
            return;
        }

        for (int i = 0; i < importados.size(); i++) {
            flowLibros.getChildren().add(crearTarjetaLibro(importados.get(i), COLORES[i % COLORES.length]));
        }
    }

    private VBox crearTarjetaLibro(Libro libro, String colorBtn) {
        VBox card = new VBox(10);
        card.setPrefWidth(210);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");
        card.setPadding(new Insets(18));

        // Imagen con badge "🌍 IMPORTADO"
        StackPane imgPane = ImagenUtil.crearPanelImagen(libro.getImagen(), 174, 220);
        Label lblImportado = new Label("🌍 IMPORTADO");
        lblImportado.setStyle("-fx-background-color: " + colorBtn + "; -fx-background-radius: 6; " +
                "-fx-font-size: 10px; -fx-font-weight: 800; -fx-padding: 4 8; -fx-text-fill: white;");
        StackPane.setAlignment(lblImportado, Pos.TOP_RIGHT);
        imgPane.getChildren().add(lblImportado);

        Label lblTitulo = new Label(libro.getTitulo());
        lblTitulo.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: #0f172a;");
        // color set inline
        lblTitulo.setWrapText(true);

        Label lblAutor = new Label(libro.getAutor());
        lblAutor.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        // color set inline

        Label lblCategoria = new Label(libro.getCategoria());
        lblCategoria.setStyle("-fx-background-color: #ede9fe; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: 700; -fx-padding: 3 8; -fx-text-fill: #7c3aed;");
        // color set inline

        Label lblPrecio = new Label(String.format("COP %,.0f", libro.getPrecio()));
        lblPrecio.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: #7c3aed;");
        // color set inline

        // Pega esto en crearTarjetaLibro() de ambos controladores,
// justo antes de crear btnCarrito:

        boolean hayStock = libro.getStock() > 0;
        Label lblStock = new Label(hayStock ? "Stock: " + libro.getStock() : "Sin stock");
        lblStock.setStyle("-fx-font-size: 12px; -fx-font-weight: 700;");
        lblStock.setTextFill(hayStock ? Color.web("#10b981") : Color.web("#ef4444"));

// Y modifica el btnCarrito así:
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
            ok.setTitle("Carrito"); ok.setHeaderText(null);
            ok.setContentText("\"" + libro.getTitulo() + "\" agregado al carrito.");
            ok.showAndWait();
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        }
    }
}