package com.alethanabooks.controlador;

import com.alethanabooks.interfaces.Descargable;
import com.alethanabooks.modelo.Libro;
import com.alethanabooks.modelo.LibroDigital;
import com.alethanabooks.modelo.SesionActual;
import com.alethanabooks.service.CatalogoService;
import com.alethanabooks.Util.ImagenUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Muestra solo los libros de tipo LibroDigital.
 * Permite filtrar por formato (PDF / EPUB / MOBI).
 * Usa la interfaz Descargable para verificar disponibilidad.
 */
public class LibrosDigitalesController implements Initializable {

    @FXML private FlowPane gridDigitales;
    @FXML private Label    lblConteo;
    @FXML private Button   btnTodos, btnPDF, btnEPUB, btnMOBI;

    private final CatalogoService catalogoService = new CatalogoService();

    private static final String ESTILO_ACTIVO =
            "-fx-background-color: #7c3aed; -fx-background-radius: 8; " +
                    "-fx-font-size: 13px; -fx-font-weight: 800; -fx-padding: 7 18;";
    private static final String ESTILO_INACTIVO =
            "-fx-background-color: #f1f5f9; -fx-background-radius: 8; " +
                    "-fx-border-color: #e5e7eb; -fx-font-size: 13px; -fx-padding: 7 18;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        poblarGrid(null); // null = todos los formatos
    }

    @FXML private void filtrarTodos() { activar(btnTodos); poblarGrid(null); }
    @FXML private void filtrarPDF()   { activar(btnPDF);   poblarGrid("PDF"); }
    @FXML private void filtrarEPUB()  { activar(btnEPUB);  poblarGrid("EPUB"); }
    @FXML private void filtrarMOBI()  { activar(btnMOBI);  poblarGrid("MOBI"); }

    private void activar(Button activo) {
        for (Button b : new Button[]{btnTodos, btnPDF, btnEPUB, btnMOBI}) {
            b.setStyle(ESTILO_INACTIVO);
            b.setTextFill(Color.web("#334155"));
        }
        activo.setStyle(ESTILO_ACTIVO);
        activo.setTextFill(Color.WHITE);
    }

    private void poblarGrid(String formatoFiltro) {
        gridDigitales.getChildren().clear();

        // Filtrar catálogo: solo LibroDigital, disponibles para descarga
        List<Libro> digitales = catalogoService.obtenerTodos().stream()
                .filter(l -> l instanceof LibroDigital ld && ld.estaDisponibleParaDescarga())
                .filter(l -> {
                    if (formatoFiltro == null) return true;
                    return formatoFiltro.equalsIgnoreCase(((LibroDigital) l).getFormato());
                })
                .toList();

        lblConteo.setText(digitales.isEmpty()
                ? "No hay e-books disponibles"
                : digitales.size() + " e-book" + (digitales.size() == 1 ? "" : "s") + " disponibles");

        if (digitales.isEmpty()) {
            Label vacio = new Label("No hay libros digitales con ese formato aún.");
            vacio.setStyle("-fx-font-size: 15px;");
            vacio.setTextFill(Color.web("#94a3b8"));
            gridDigitales.getChildren().add(vacio);
            return;
        }

        String[] colores = {"#7c3aed", "#ec4899", "#10b981", "#f59e0b", "#0ea574"};
        for (int i = 0; i < digitales.size(); i++) {
            gridDigitales.getChildren().add(
                    crearTarjetaDigital((LibroDigital) digitales.get(i), colores[i % colores.length]));
        }
    }

    private VBox crearTarjetaDigital(LibroDigital libro, String colorBtn) {
        VBox card = new VBox(10);
        card.setPrefWidth(195);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(124,58,237,0.07), 8, 0, 0, 2);");
        card.setPadding(new Insets(16));

        // Imagen con badge de formato
        StackPane imgPane = ImagenUtil.crearPanelImagen(libro.getImagen(), 163, 210);
        Label badgeFormato = new Label("📄 " + libro.getFormato());
        badgeFormato.setStyle("-fx-background-color: #7c3aed; -fx-background-radius: 6; " +
                "-fx-font-size: 10px; -fx-font-weight: 900; -fx-padding: 3 8;");
        badgeFormato.setTextFill(Color.WHITE);
        javafx.scene.layout.StackPane.setAlignment(badgeFormato, javafx.geometry.Pos.TOP_LEFT);
        imgPane.getChildren().add(badgeFormato);

        Label badgeDigital = new Label("⬇ Digital");
        badgeDigital.setStyle("-fx-background-color: #10b981; -fx-background-radius: 6; " +
                "-fx-font-size: 10px; -fx-font-weight: 900; -fx-padding: 3 8;");
        badgeDigital.setTextFill(Color.WHITE);
        javafx.scene.layout.StackPane.setAlignment(badgeDigital, javafx.geometry.Pos.TOP_RIGHT);
        imgPane.getChildren().add(badgeDigital);

        Label lblTitulo = new Label(libro.getTitulo());
        lblTitulo.setStyle("-fx-font-size: 15px; -fx-font-weight: 800;");
        lblTitulo.setTextFill(Color.web("#0f172a"));
        lblTitulo.setWrapText(true);

        Label lblAutor = new Label(libro.getAutor());
        lblAutor.setStyle("-fx-font-size: 12px;");
        lblAutor.setTextFill(Color.web("#64748b"));

        Label lblCategoria = new Label(libro.getCategoria());
        lblCategoria.setStyle("-fx-background-color: #ede9fe; -fx-background-radius: 6; " +
                "-fx-font-size: 11px; -fx-font-weight: 700; -fx-padding: 3 8;");
        lblCategoria.setTextFill(Color.web("#7c3aed"));

        // Precio con descuento digital visible
        double precioOriginal = libro.getPrecio();
        double precioDigital  = precioOriginal * 0.90;

        HBox precios = new HBox(8);
        precios.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label lblPrecioOriginal = new Label(String.format("COP %,.0f", precioOriginal));
        lblPrecioOriginal.setStyle("-fx-font-size: 12px; -fx-strikethrough: true;");
        lblPrecioOriginal.setTextFill(Color.web("#94a3b8"));
        Label lblPrecioFinal = new Label(String.format("COP %,.0f", precioDigital));
        lblPrecioFinal.setStyle("-fx-font-size: 18px; -fx-font-weight: 900;");
        lblPrecioFinal.setTextFill(Color.web("#7c3aed"));
        precios.getChildren().addAll(lblPrecioOriginal, lblPrecioFinal);

        Label lblDescuento = new Label("−10% e-book");
        lblDescuento.setStyle("-fx-background-color: #dcfce7; -fx-background-radius: 6; " +
                "-fx-font-size: 10px; -fx-font-weight: 800; -fx-padding: 2 6;");
        lblDescuento.setTextFill(Color.web("#16a34a"));

        Label lblDescargaInstantanea = new Label("⚡ Descarga instantánea");
        lblDescargaInstantanea.setStyle("-fx-font-size: 11px;");
        lblDescargaInstantanea.setTextFill(Color.web("#64748b"));

        Button btnCarrito = new Button("Agregar al carrito");
        btnCarrito.setMaxWidth(Double.MAX_VALUE);
        btnCarrito.setTextFill(Color.WHITE);
        btnCarrito.setStyle("-fx-background-color: " + colorBtn +
                "; -fx-background-radius: 10; -fx-font-size: 13px; -fx-font-weight: 800;");
        btnCarrito.setOnAction(e -> agregarAlCarrito(libro));

        card.getChildren().addAll(imgPane, lblTitulo, lblAutor, lblCategoria,
                precios, lblDescuento, lblDescargaInstantanea, btnCarrito);
        return card;
    }

    private void agregarAlCarrito(Libro libro) {
        if (!SesionActual.haySesion()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "Inicia sesión para agregar libros al carrito.").showAndWait();
            return;
        }
        try {
            SesionActual.getCarritoService().agregarLibro(libro, 1);
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Carrito");
            ok.setHeaderText(null);
            ok.setContentText("\"" + libro.getTitulo() + "\" agregado al carrito.\n" +
                    "Se aplicará automáticamente el 10% de descuento digital al pagar.");
            ok.showAndWait();
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        }
    }
}
