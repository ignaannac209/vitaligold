package main.java.com.ingsoft.vitaligold.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.com.ingsoft.vitaligold.dto.request.RegistroRequest;
import main.java.com.ingsoft.vitaligold.service.AuthService;
import main.java.com.ingsoft.vitaligold.util.SceneManager;

/**
  Controlador de la pantalla de Registro.
 */
public class RegistroController implements Initializable {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final AuthService authService;
    private final SceneManager sceneManager;

    @FXML
    private TextField txtFieldNombre;

    @FXML
    private TextField txtFieldUsuario;

    @FXML
    private PasswordField txtFieldPassword;

    @FXML
    private TextField txtFieldEmail;

    @FXML
    private Label lblMensajeError;

    public RegistroController(AuthService authService, SceneManager sceneManager) {
        this.authService = authService;
        this.sceneManager = sceneManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblMensajeError.setText("");
    }

    @FXML
    private void handleGuardar(ActionEvent event) throws Exception {
        RegistroRequest registroRequest = leerFormulario();

        String errorValidacion = validar(registroRequest);
        if (errorValidacion != null) {
            lblMensajeError.setText(errorValidacion);
            return;
        }

        try {
            authService.registrar(registroRequest);
        } catch (SQLException | IllegalStateException | IllegalArgumentException e) {
            String mensaje = e.getMessage() != null ? e.getMessage() : "No fue posible completar el registro.";
            lblMensajeError.setText(mensaje);
            mostrarAlerta(Alert.AlertType.ERROR, "Error al registrar", mensaje);
            return;
        }

        lblMensajeError.setText("");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso",
                "¡Cuenta creada correctamente! Ahora puedes iniciar sesión, " + registroRequest.getNombre() + ".");

        sceneManager.showLoginView();
    }

    @FXML
    private void handleCancelar(ActionEvent event) throws Exception {
        sceneManager.showLoginView();
    }

    private RegistroRequest leerFormulario() {
        String nombre = txtFieldNombre.getText() == null ? "" : txtFieldNombre.getText().trim();
        String usuario = txtFieldUsuario.getText() == null ? "" : txtFieldUsuario.getText().trim();
        String password = txtFieldPassword.getText() == null ? "" : txtFieldPassword.getText().trim();
        String email = txtFieldEmail.getText() == null ? "" : txtFieldEmail.getText().trim();
        return new RegistroRequest(nombre, usuario, password, email);
    }

    private String validar(RegistroRequest registroRequest) {
        if (registroRequest.getNombre().isEmpty() || registroRequest.getUsuario().isEmpty()
                || registroRequest.getPassword().isEmpty() || registroRequest.getEmail().isEmpty()) {
            return "Todos los campos son obligatorios.";
        }
        if (registroRequest.getPassword().length() < 4) {
            return "La contraseña debe tener al menos 4 caracteres.";
        }
        if (!EMAIL_PATTERN.matcher(registroRequest.getEmail()).matches()) {
            return "Ingresa un correo electrónico válido.";
        }
        return null;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
