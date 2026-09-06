package main.java.com.ingsoft.vitaligold.dto.response;

import java.time.LocalDate;
import java.time.Period;

/**
 * Datos de un paciente ya guardado, tal como se devuelven
 * después de una operación exitosa contra la base de datos.
 */
public class PacienteResponse {

    private String dpi;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String genero;
    private String tipoSangre;

    public PacienteResponse(String dpi, String nombres, String apellidos, LocalDate fechaNacimiento,
            String genero, String tipoSangre) {
        this.dpi = dpi;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.tipoSangre = tipoSangre;
    }

    public String getDpi() {
        return dpi;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    /** Edad calculada automáticamente a partir de la fecha de nacimiento. */
    public int getEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
