package main.java.com.ingsoft.vitaligold.service;

import java.sql.SQLException;
import main.java.com.ingsoft.vitaligold.dto.request.LoginRequest;
import main.java.com.ingsoft.vitaligold.dto.request.RegistroRequest;
import main.java.com.ingsoft.vitaligold.dto.response.LoginResponse;
import main.java.com.ingsoft.vitaligold.repository.AuthRepository;
import main.java.com.ingsoft.vitaligold.security.jbcrypt.BCrypt;

/**
 * Revisa las credenciales de un usuario contra la base de datos,
 * comparando la contraseña escrita con el hash guardado usando BCrypt.
 */
public class AuthService {

    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) throws SQLException {
        if (loginRequest == null) {
            throw new IllegalArgumentException("Credenciales vacías");
        }
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()
                || loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            throw new IllegalArgumentException("El correo o la contraseña no pueden estar vacíos");
        }

        LoginResponse response = authRepository.findUserByEmail(loginRequest);

        if (response == null) {
            throw new IllegalStateException("Usuario no encontrado");
        }

        String contrasenaHashed = response.getContrasenaHash();

        if (contrasenaHashed == null || !BCrypt.checkpw(loginRequest.getPassword(), contrasenaHashed)) {
            throw new IllegalStateException("Contraseña incorrecta");
        }

        return response;
    }

    /**
     * Registra un nuevo usuario en la base de datos, con el rol
     * "Empleado" asignado por defecto.
     */
    public void registrar(RegistroRequest registroRequest) throws SQLException {
        if (registroRequest == null) {
            throw new IllegalArgumentException("Datos de registro vacíos");
        }
        if (registroRequest.getNombre() == null || registroRequest.getNombre().isBlank()
                || registroRequest.getEmail() == null || registroRequest.getEmail().isBlank()
                || registroRequest.getPassword() == null || registroRequest.getPassword().isBlank()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }

        String contrasenaHash = BCrypt.hashpw(registroRequest.getPassword(), BCrypt.gensalt());

        try {
            authRepository.registrarUsuario(registroRequest, contrasenaHash);
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new IllegalStateException("Ya existe una cuenta registrada con ese correo.");
            }
            throw e;
        }
    }
}
