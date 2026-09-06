package main.java.com.ingsoft.vitaligold.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.java.com.ingsoft.vitaligold.model.Auth;
import main.java.com.ingsoft.vitaligold.repository.PacienteRepository;
import main.java.com.ingsoft.vitaligold.service.PacienteService;
import main.java.com.ingsoft.vitaligold.util.SceneManager;

/**
  Controlador del Menú Principal.
 */
public class MainMenuController implements Initializable {

    private static final String VIEW_PACIENTES = "/main/resources/view/paciente-view.fxml";

    private final SceneManager sceneManager;
    private final Auth usuarioAutenticado;
    private final PacienteService pacienteService;

    @FXML
    private Label lblUsuarioActivo;

    @FXML
    private Label lblTituloSeccion;

    @FXML
    private StackPane paneContenido;

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnGestionPacientes;

    public MainMenuController(SceneManager sceneManager, Auth usuarioAutenticado) {
        this.sceneManager = sceneManager;
        this.usuarioAutenticado = usuarioAutenticado;
        this.pacienteService = new PacienteService(new PacienteRepository());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String nombre = usuarioAutenticado != null ? usuarioAutenticado.getNombreCompleto() : "Usuario";
        String rol = usuarioAutenticado != null ? usuarioAutenticado.getRol() : null;
        lblUsuarioActivo.setText(rol == null || rol.isBlank() ? nombre : nombre + " (" + rol + ")");
        abrirInicio();
    }

    /** Muestra el panel de Inicio con un resumen rápido del sistema. */
    @FXML
    private void handleInicio(ActionEvent event) {
        abrirInicio();
    }

    /** Abre el módulo "Gestión de Pacientes" dentro del mismo panel de contenido. */
    @FXML
    private void handleGestionPacientes(ActionEvent event) {
        abrirGestionPacientes();
    }

    private void abrirInicio() {
        marcarBotonActivo(btnInicio);
        lblTituloSeccion.setText("Inicio");

        String nombre = usuarioAutenticado != null ? usuarioAutenticado.getNombreCompleto() : "Usuario";

        int totalPacientes;
        try {
            totalPacientes = pacienteService.listar().size();
        } catch (SQLException e) {
            totalPacientes = -1;
        }

        Label saludo = new Label("Bienvenido(a), " + nombre);
        saludo.getStyleClass().add("inicio-saludo");

        Label descripcion = new Label(
                "Desde aquí puedes administrar la admisión de pacientes del centro de salud.");
        descripcion.getStyleClass().add("inicio-descripcion");
        descripcion.setWrapText(true);

        Label numeroStat = new Label(totalPacientes >= 0 ? String.valueOf(totalPacientes) : "--");
        numeroStat.getStyleClass().add("inicio-stat-numero");

        Label etiquetaStat = new Label("Pacientes registrados");
        etiquetaStat.getStyleClass().add("inicio-stat-etiqueta");

        VBox tarjetaStat = new VBox(4, numeroStat, etiquetaStat);
        tarjetaStat.getStyleClass().add("inicio-stat-card");
        tarjetaStat.setPadding(new Insets(18, 26, 18, 26));

        Button irAPacientes = new Button("Ir a Gestión de Pacientes");
        irAPacientes.getStyleClass().add("boton-inicio-cta");
        irAPacientes.setOnAction(e -> abrirGestionPacientes());

        VBox contenido = new VBox(18, saludo, descripcion, tarjetaStat, irAPacientes);
        contenido.getStyleClass().add("inicio-root");

        paneContenido.getChildren().setAll(contenido);
    }

    private void abrirGestionPacientes() {
        marcarBotonActivo(btnGestionPacientes);
        lblTituloSeccion.setText("Gestión de Pacientes");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_PACIENTES));
            loader.setControllerFactory(clazz -> {
                if (clazz == PacienteController.class) {
                    String rol = usuarioAutenticado != null ? usuarioAutenticado.getRol() : null;
                    return new PacienteController(pacienteService, rol);
                }
                return instantiate(clazz);
            });
            Parent vistaPacientes = loader.load();
            paneContenido.getChildren().setAll(vistaPacientes);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al abrir el módulo");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar Gestión de Pacientes: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /** Resalta en el menú lateral la sección que está activa. */
    private void marcarBotonActivo(Button botonActivo) {
        btnInicio.getStyleClass().remove("boton-menu-activo");
        btnGestionPacientes.getStyleClass().remove("boton-menu-activo");
        if (!botonActivo.getStyleClass().contains("boton-menu-activo")) {
            botonActivo.getStyleClass().add("boton-menu-activo");
        }
    }

    private Object instantiate(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo crear el controlador " + clazz.getName(), e);
        }
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) throws Exception {
        paneContenido.getChildren().clear();
        sceneManager.showLoginView();
    }
}
