package main.java.com.ingsoft.vitaligold.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import main.java.com.ingsoft.vitaligold.dto.request.PacienteRequest;
import main.java.com.ingsoft.vitaligold.model.Paciente;
import main.java.com.ingsoft.vitaligold.repository.PacienteRepository;

/**
 * Reglas de negocio y validaciones del módulo de Admisión de
 * Pacientes.
 */
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<Paciente> listar() throws SQLException {
        return pacienteRepository.findAll();
    }

    /** Valida y crea un nuevo paciente. */
    public void registrar(PacienteRequest request) throws SQLException {
        validarCampos(request);

        try {
            if (pacienteRepository.existsByDpi(request.getDpi())) {
                throw new IllegalStateException(
                        "Ya existe un paciente registrado con el DPI " + request.getDpi() + ".");
            }
            pacienteRepository.guardar(request);
        } catch (SQLException e) {
            throw traducirError(e);
        }
    }

    /** Valida y actualiza un paciente existente. */
    public void actualizar(PacienteRequest request) throws SQLException {
        validarCampos(request);

        try {
            if (!pacienteRepository.existsByDpi(request.getDpi())) {
                throw new IllegalStateException(
                        "No existe ningún paciente con el DPI " + request.getDpi() + ".");
            }
            pacienteRepository.actualizar(request);
        } catch (SQLException e) {
            throw traducirError(e);
        }
    }

    /** Elimina un paciente por su DPI. */
    public void eliminar(String dpi) throws SQLException {
        if (dpi == null || dpi.isBlank()) {
            throw new IllegalArgumentException("Selecciona un paciente de la tabla para eliminarlo.");
        }
        try {
            pacienteRepository.eliminar(dpi);
        } catch (SQLException e) {
            throw traducirError(e);
        }
    }

    /** Valida los campos obligatorios y sus formatos antes de enviarlos a la base de datos. */
    private void validarCampos(PacienteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del paciente están vacíos.");
        }
        if (esVacio(request.getDpi())) {
            throw new IllegalArgumentException("El Código de Seguridad Social/DPI es obligatorio.");
        }
        if (!request.getDpi().matches("\\d{8,13}")) {
            throw new IllegalArgumentException("El DPI debe contener solo números (entre 8 y 13 dígitos).");
        }
        if (esVacio(request.getNombres())) {
            throw new IllegalArgumentException("Los nombres son obligatorios.");
        }
        if (esVacio(request.getApellidos())) {
            throw new IllegalArgumentException("Los apellidos son obligatorios.");
        }
        if (request.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("Debes seleccionar la fecha de nacimiento.");
        }
        if (request.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser una fecha futura.");
        }
        if (esVacio(request.getGenero())) {
            throw new IllegalArgumentException("Debes seleccionar el género del paciente.");
        }
        if (esVacio(request.getTipoSangre())) {
            throw new IllegalArgumentException("Debes seleccionar el tipo de sangre del paciente.");
        }
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    /** Convierte errores comunes de SQL (llave duplicada, etc.) en mensajes claros. */
    private IllegalStateException traducirError(SQLException e) {
        String mensaje = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        if (mensaje.contains("duplicate") || mensaje.contains("primary")) {
            return new IllegalStateException("Ya existe un paciente registrado con ese DPI.");
        }
        return new IllegalStateException("Ocurrió un error al comunicarse con la base de datos: " + e.getMessage());
    }
}
