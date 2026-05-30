package com.alethanabooks.controlador;

import com.alethanabooks.modelo.SesionActual;
import com.alethanabooks.modelo.Opinion;
import com.alethanabooks.persistence.OpinionRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class OpinionController implements Initializable {

    @FXML private TextArea txtOpinion;
    @FXML private RadioButton rb1, rb2, rb3, rb4, rb5;
    @FXML private ToggleGroup grupoEstrellas;
    @FXML private Label lblMensaje;
    @FXML private VBox listaOpiniones;

    private final OpinionRepository opinionRepository = new OpinionRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarOpiniones();
    }

    @FXML
    private void onPublicar() {
        String texto = txtOpinion.getText().trim();
        if (texto.isEmpty()) {
            lblMensaje.setText("Escribe tu opinión antes de publicar.");
            lblMensaje.setTextFill(Color.web("#ef4444"));
            return;
        }
        if (texto.length() < 10) {
            lblMensaje.setText("La opinión debe tener al menos 10 caracteres.");
            lblMensaje.setTextFill(Color.web("#ef4444"));
            return;
        }

        int estrellas = rb1.isSelected() ? 1 : rb2.isSelected() ? 2 :
                rb3.isSelected() ? 3 : rb4.isSelected() ? 4 : 5;

        String nombre = SesionActual.haySesion()
                ? SesionActual.getUsuario().getNombre()
                : "Anónimo";

        Opinion opinion = new Opinion(UUID.randomUUID().toString(), nombre, texto, estrellas);
        opinionRepository.agregar(opinion);

        txtOpinion.clear();
        rb3.setSelected(true);
        lblMensaje.setText("Opinión publicada. ¡Gracias!");
        lblMensaje.setTextFill(Color.web("#10b981"));
        cargarOpiniones();
    }

    private void cargarOpiniones() {
        listaOpiniones.getChildren().clear();
        List<Opinion> opiniones = opinionRepository.obtenerTodas();

        if (opiniones.isEmpty()) {
            Label lbl = new Label("Sé el primero en dejar una opinión.");
            lbl.setStyle("-fx-font-size: 14px;");
            lbl.setTextFill(Color.web("#94a3b8"));
            listaOpiniones.getChildren().add(lbl);
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (int i = opiniones.size() - 1; i >= 0; i--) {
            listaOpiniones.getChildren().add(crearTarjetaOpinion(opiniones.get(i), fmt));
        }
    }

    private VBox crearTarjetaOpinion(Opinion op, DateTimeFormatter fmt) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-border-color: #e5e7eb; -fx-border-radius: 12;");
        card.setPadding(new Insets(16, 20, 16, 20));

        String estrellas = "".repeat(op.getEstrellas()) +
                "☆".repeat(5 - op.getEstrellas());

        HBox encabezado = new HBox(12);
        Label lblNombre = new Label(op.getNombreUsuario());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-font-weight: 900;");
        lblNombre.setTextFill(Color.web("#0f172a"));

        Label lblEstrellas = new Label(estrellas);
        lblEstrellas.setStyle("-fx-font-size: 14px;");

        Label lblFecha = new Label(op.getFecha() != null ? op.getFecha().format(fmt) : "");
        lblFecha.setStyle("-fx-font-size: 12px;");
        lblFecha.setTextFill(Color.web("#94a3b8"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        encabezado.getChildren().addAll(lblNombre, lblEstrellas, spacer, lblFecha);

        Label lblTexto = new Label(op.getTexto());
        lblTexto.setStyle("-fx-font-size: 14px;");
        lblTexto.setTextFill(Color.web("#334155"));
        lblTexto.setWrapText(true);

        card.getChildren().addAll(encabezado, lblTexto);
        return card;
    }
}