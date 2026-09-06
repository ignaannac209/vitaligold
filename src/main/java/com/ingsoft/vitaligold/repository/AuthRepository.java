package main.java.com.ingsoft.vitaligold.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import main.java.com.ingsoft.vitaligold.config.DataBaseConnection;
import main.java.com.ingsoft.vitaligold.dto.request.LoginRequest;
import main.java.com.ingsoft.vitaligold.dto.request.RegistroRequest;
import main.java.com.ingsoft.vitaligold.dto.response.LoginResponse;

/**
 * Consulta y guarda en la base de datos todo con
 * el inicio de sesión y el registro de usuarios.
 */
public class AuthRepository {

    /** Rol que se asigna automáticamente a quien se registra desde la app. */
    private static final String ROL_POR_DEFECTO = "Empleado";

    public AuthRepository() {
    }

    /**
     * Busca un usuario por su correo electrónico, junto con el
     * nombre de su rol.
     */
    public LoginResponse findUserByEmail(LoginRequest loginRequest) throws SQLException {
        String sql = "SELECT u.nombre, u.apellido, u.contrasena_hash, r.nombre_rol "
                + "FROM usuarios AS u "
                + "JOIN roles AS r ON r.id_rol = u.id_rol "
                + "WHERE u.email = ?";

        try (PreparedStatement pstm = DataBaseConnection.getConnectionDataBase().prepareStatement(sql)) {
            pstm.setString(1, loginRequest.getEmail());

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new LoginResponse(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("contrasena_hash"),
                            rs.getString("nombre_rol")
                    );
                }
            }
        }

        return null;
    }

    /**
     * Guarda un nuevo usuario con el rol "Empleado" por defecto.
     */
    public void registrarUsuario(RegistroRequest registroRequest, String contrasenaHash) throws SQLException {
        Connection connection = DataBaseConnection.getConnectionDataBase();

        int idRol = obtenerIdRolPorDefecto(connection);
        String[] partesNombre = dividirNombreCompleto(registroRequest.getNombre());

        String sql = "INSERT INTO usuarios (nombre, apellido, usuario, email, contrasena_hash, id_rol) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, partesNombre[0]);
            pstm.setString(2, partesNombre[1]);
            pstm.setString(3, registroRequest.getUsuario());
            pstm.setString(4, registroRequest.getEmail());
            pstm.setString(5, contrasenaHash);
            pstm.setInt(6, idRol);
            pstm.executeUpdate();
        }
    }

    /** Busca el id del rol "Empleado", el que se asigna a los usuarios nuevos. */
    private int obtenerIdRolPorDefecto(Connection connection) throws SQLException {
        String sql = "SELECT id_rol FROM roles WHERE nombre_rol = ?";

        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, ROL_POR_DEFECTO);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_rol");
                }
            }
        }

        throw new SQLException("No existe el rol por defecto '" + ROL_POR_DEFECTO
                + "'. Revisa que la tabla roles tenga ese registro.");
    }

    /** Separa "Nombre Apellido" en dos partes para guardarlas por separado. */
    private String[] dividirNombreCompleto(String nombreCompleto) {
        String limpio = nombreCompleto.trim();
        int indiceEspacio = limpio.indexOf(' ');

        if (indiceEspacio == -1) {
            return new String[]{limpio, ""};
        }
        return new String[]{limpio.substring(0, indiceEspacio), limpio.substring(indiceEspacio + 1).trim()};
    }
}
