package main.java.com.ingsoft.vitaligold.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import main.java.com.ingsoft.vitaligold.config.DataBaseConnection;
import main.java.com.ingsoft.vitaligold.dto.request.PacienteRequest;
import main.java.com.ingsoft.vitaligold.model.Paciente;

/**
 * Acceso a datos de la tabla
 */
public class PacienteRepository {

    private static final String SQL_SELECT_ALL =
            "SELECT dpi, nombres, apellidos, fecha_nacimiento, genero, tipo_sangre "
            + "FROM pacientes ORDER BY apellidos, nombres";

    private static final String SQL_EXISTS =
            "SELECT 1 FROM pacientes WHERE dpi = ?";

    private static final String SQL_INSERT =
            "INSERT INTO pacientes (dpi, nombres, apellidos, fecha_nacimiento, genero, tipo_sangre) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE pacientes SET nombres = ?, apellidos = ?, fecha_nacimiento = ?, genero = ?, tipo_sangre = ? "
            + "WHERE dpi = ?";

    private static final String SQL_DELETE =
            "DELETE FROM pacientes WHERE dpi = ?";

    /** Lee (SELECT) todos los pacientes registrados. */
    public List<Paciente> findAll() throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        Connection connection = DataBaseConnection.getConnectionDataBase();

        try (PreparedStatement pstm = connection.prepareStatement(SQL_SELECT_ALL);
                ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                pacientes.add(mapRow(rs));
            }
        }
        return pacientes;
    }

    /** Indica si ya existe un paciente registrado con ese DPI. */
    public boolean existsByDpi(String dpi) throws SQLException {
        Connection connection = DataBaseConnection.getConnectionDataBase();

        try (PreparedStatement pstm = connection.prepareStatement(SQL_EXISTS)) {
            pstm.setString(1, dpi);
            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Crea (INSERT) un nuevo paciente. */
    public void guardar(PacienteRequest request) throws SQLException {
        Connection connection = DataBaseConnection.getConnectionDataBase();

        try (PreparedStatement pstm = connection.prepareStatement(SQL_INSERT)) {
            pstm.setString(1, request.getDpi());
            pstm.setString(2, request.getNombres());
            pstm.setString(3, request.getApellidos());
            pstm.setDate(4, Date.valueOf(request.getFechaNacimiento()));
            pstm.setString(5, request.getGenero());
            pstm.setString(6, request.getTipoSangre());
            pstm.executeUpdate();
        }
    }

    /** Actualiza (UPDATE) los datos de un paciente existente, identificado por su DPI. */
    public void actualizar(PacienteRequest request) throws SQLException {
        Connection connection = DataBaseConnection.getConnectionDataBase();

        try (PreparedStatement pstm = connection.prepareStatement(SQL_UPDATE)) {
            pstm.setString(1, request.getNombres());
            pstm.setString(2, request.getApellidos());
            pstm.setDate(3, Date.valueOf(request.getFechaNacimiento()));
            pstm.setString(4, request.getGenero());
            pstm.setString(5, request.getTipoSangre());
            pstm.setString(6, request.getDpi());
            pstm.executeUpdate();
        }
    }

    /** Elimina (DELETE) un paciente por su DPI. */
    public void eliminar(String dpi) throws SQLException {
        Connection connection = DataBaseConnection.getConnectionDataBase();

        try (PreparedStatement pstm = connection.prepareStatement(SQL_DELETE)) {
            pstm.setString(1, dpi);
            pstm.executeUpdate();
        }
    }

    private Paciente mapRow(ResultSet rs) throws SQLException {
        return new Paciente(
                rs.getString("dpi"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getDate("fecha_nacimiento").toLocalDate(),
                rs.getString("genero"),
                rs.getString("tipo_sangre")
        );
    }
}
