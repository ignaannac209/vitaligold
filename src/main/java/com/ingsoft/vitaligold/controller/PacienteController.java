package main.java.com.ingsoft.vitaligold.controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.com.ingsoft.vitaligold.dto.request.PacienteRequest;
import main.java.com.ingsoft.vitaligold.model.Paciente;
import main.java.com.ingsoft.vitaligold.service.PacienteService;

/**
 * Controlador del módulo "Gestión de Pacientes" (Admisión de Pacientes).
 * Maneja la carga de la tabla, el guardado, la actualización, la
 * eliminación y la validación de los datos del formulario.
 */
public class PacienteController implements Initializable {

    private static final ObservableList<String> GENEROS =
            FXCollections.observableArrayList("Masculino", "Femenino", "Otro");

    private static final ObservableList<String> TIPOS_SANGRE =
            FXCollections.observableArrayList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

    /** Rol al que se le restringe el registro de pacientes nuevos: solo puede leer, actualizar y eliminar. */
    private static final String ROL_SIN_PERMISO_CREAR = "Administrador";

    private final PacienteService pacienteService;
    private final String rolUsuario;
    private final ObservableList<Paciente> pacientes = FXCollections.observableArrayList();

    @FXML
    private TextField txtFieldDpi;

    @FXML
    private TextField txtFieldNombres;

    @FXML
    private TextField txtFieldApellidos;

    @FXML
    private DatePicker dateFechaNacimiento;

    @FXML
    private ComboBox<String> comboGenero;

    @FXML
    private ComboBox<String> comboTipoSangre;

    @FXML
    private Label lblMensaje;

    @FXML
    private Label lblAvisoRol;

    @FXML
    private Button btnGuardar;

    @FXML
    private TableView<Paciente> tablaPacientes;

    @FXML
    private TableColumn<Paciente, String> colDpi;

    @FXML
    private TableColumn<Paciente, String> colNombres;

    @FXML
    private TableColumn<Paciente, String> colApellidos;

    @FXML
    private TableColumn<Paciente, LocalDate> colFechaNacimiento;

    @FXML
    private TableColumn<Paciente, String> colGenero;

    @FXML
    private TableColumn<Paciente, String> colTipoSangre;

    public PacienteController(PacienteService pacienteService, String rolUsuario) {
        this.pacienteService = pacienteService;
        this.rolUsuario = rolUsuario;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboGenero.setItems(GENEROS);
        comboTipoSangre.setItems(TIPOS_SANGRE);

        colDpi.setCellValueFactory(new PropertyValueFactory<>("dpi"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colTipoSangre.setCellValueFactory(new PropertyValueFactory<>("tipoSangre"));

        tablaPacientes.setItems(pacientes);

        // Al hacer clic en una fila, se cargan sus datos en el formulario.
        tablaPacientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                cargarEnFormulario(seleccionado);
            }
        });

        aplicarRestriccionesDeRol();
        cargarPacientes();
    }

    /**
     * El rol Administrador solo puede ver, actualizar y eliminar pacientes;
     * no puede registrar (Guardar) pacientes nuevos, esa tarea es del Empleado
     * que atiende recepción.
     */
    private void aplicarRestriccionesDeRol() {
        boolean puedeCrear = !esRolSinPermisoCrear();
        btnGuardar.setDisable(!puedeCrear);

        if (!puedeCrear) {
            lblAvisoRol.setText("Modo administrador: solo puedes consultar, actualizar o eliminar "
                    + "pacientes ya registrados. El registro de pacientes nuevos lo hace el personal de recepción.");
            lblAvisoRol.setVisible(true);
            lblAvisoRol.setManaged(true);
        }
    }

    private boolean esRolSinPermisoCrear() {
        return rolUsuario != null && rolUsuario.equalsIgnoreCase(ROL_SIN_PERMISO_CREAR);
    }

    /** Lee (SELECT) todos los pacientes y refresca el TableView. */
    private void cargarPacientes() {
        try {
            pacientes.setAll(pacienteService.listar());
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar pacientes",
                    "No se pudo consultar la base de datos: " + e.getMessage());
        }
    }

    /** Botón "Guardar": crea (INSERT) un nuevo paciente. Restringido para el rol Administrador. */
    @FXML
    private void handleGuardar(ActionEvent event) {
        if (esRolSinPermisoCrear()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acción no permitida",
                    "Tu rol (Administrador) no tiene permiso para registrar pacientes nuevos. "
                    + "Solo puedes consultar, actualizar o eliminar.");
            return;
        }
        try {
            PacienteRequest request = leerFormulario();
            pacienteService.registrar(request);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente registrado",
                    "El paciente se registró correctamente en el sistema.");
            limpiarFormulario();
            cargarPacientes();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Revisa el formulario", e.getMessage());
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos", e.getMessage());
        }
    }

    /** Botón "Actualizar": actualiza (UPDATE) el paciente seleccionado. */
    @FXML
    private void handleActualizar(ActionEvent event) {
        Paciente seleccionado = tablaPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ningún paciente seleccionado",
                    "Selecciona un paciente en la tabla antes de actualizar.");
            return;
        }
        try {
            PacienteRequest request = leerFormulario();
            pacienteService.actualizar(request);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente actualizado",
                    "Los datos del paciente se actualizaron correctamente.");
            limpiarFormulario();
            cargarPacientes();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Revisa el formulario", e.getMessage());
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos", e.getMessage());
        }
    }

    /** Botón "Eliminar": pide confirmación y elimina (DELETE) al paciente seleccionado. */
    @FXML
    private void handleEliminar(ActionEvent event) {
        Paciente seleccionado = tablaPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ningún paciente seleccionado",
                    "Selecciona un paciente en la tabla antes de eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar al paciente "
                + seleccionado.getNombres() + " " + seleccionado.getApellidos() + " (DPI "
                + seleccionado.getDpi() + ")? Esta acción no se puede deshacer.");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                pacienteService.eliminar(seleccionado.getDpi());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente eliminado",
                        "El registro del paciente se eliminó correctamente.");
                limpiarFormulario();
                cargarPacientes();
            } catch (IllegalArgumentException | IllegalStateException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "No se pudo eliminar", e.getMessage());
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos", e.getMessage());
            }
        }
    }

    /** Botón "Limpiar/Nuevo": vacía el formulario para capturar un nuevo paciente. */
    @FXML
    private void handleLimpiar(ActionEvent event) {
        limpiarFormulario();
        tablaPacientes.getSelectionModel().clearSelection();
    }

    private PacienteRequest leerFormulario() {
        String dpi = textoDe(txtFieldDpi);
        String nombres = textoDe(txtFieldNombres);
        String apellidos = textoDe(txtFieldApellidos);
        LocalDate fechaNacimiento = dateFechaNacimiento.getValue();
        String genero = comboGenero.getValue();
        String tipoSangre = comboTipoSangre.getValue();

        return new PacienteRequest(dpi, nombres, apellidos, fechaNacimiento, genero, tipoSangre);
    }

    private void cargarEnFormulario(Paciente paciente) {
        txtFieldDpi.setText(paciente.getDpi());
        txtFieldDpi.setDisable(true); // el DPI es la llave primaria: no se edita en una actualización
        txtFieldNombres.setText(paciente.getNombres());
        txtFieldApellidos.setText(paciente.getApellidos());
        dateFechaNacimiento.setValue(paciente.getFechaNacimiento());
        comboGenero.setValue(paciente.getGenero());
        comboTipoSangre.setValue(paciente.getTipoSangre());
        lblMensaje.setText("");
    }

    private void limpiarFormulario() {
        txtFieldDpi.clear();
        txtFieldDpi.setDisable(false);
        txtFieldNombres.clear();
        txtFieldApellidos.clear();
        dateFechaNacimiento.setValue(null);
        comboGenero.setValue(null);
        comboTipoSangre.setValue(null);
        lblMensaje.setText("");
    }

    private String textoDe(TextField campo) {
        return campo.getText() == null ? "" : campo.getText().trim();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        lblMensaje.setText(mensaje);
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
