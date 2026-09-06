package main.java.com.ingsoft.vitaligold.model;

import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Representa a un paciente admitido en el Centro de Salud.
 */
public class Paciente {

    private final StringProperty dpi = new SimpleStringProperty();
    private final StringProperty nombres = new SimpleStringProperty();
    private final StringProperty apellidos = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fechaNacimiento = new SimpleObjectProperty<>();
    private final StringProperty genero = new SimpleStringProperty();
    private final StringProperty tipoSangre = new SimpleStringProperty();

    public Paciente() {
    }

    public Paciente(String dpi, String nombres, String apellidos, LocalDate fechaNacimiento,
            String genero, String tipoSangre) {
        setDpi(dpi);
        setNombres(nombres);
        setApellidos(apellidos);
        setFechaNacimiento(fechaNacimiento);
        setGenero(genero);
        setTipoSangre(tipoSangre);
    }

    public String getDpi() {
        return dpi.get();
    }

    public void setDpi(String dpi) {
        this.dpi.set(dpi);
    }

    public StringProperty dpiProperty() {
        return dpi;
    }

    public String getNombres() {
        return nombres.get();
    }

    public void setNombres(String nombres) {
        this.nombres.set(nombres);
    }

    public StringProperty nombresProperty() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos.get();
    }

    public void setApellidos(String apellidos) {
        this.apellidos.set(apellidos);
    }

    public StringProperty apellidosProperty() {
        return apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento.get();
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento.set(fechaNacimiento);
    }

    public ObjectProperty<LocalDate> fechaNacimientoProperty() {
        return fechaNacimiento;
    }

    public String getGenero() {
        return genero.get();
    }

    public void setGenero(String genero) {
        this.genero.set(genero);
    }

    public StringProperty generoProperty() {
        return genero;
    }

    public String getTipoSangre() {
        return tipoSangre.get();
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre.set(tipoSangre);
    }

    public StringProperty tipoSangreProperty() {
        return tipoSangre;
    }

    /** Calcula la edad actual del paciente a partir de su fecha de nacimiento. */
    public int getEdad() {
        if (getFechaNacimiento() == null) {
            return 0;
        }
        return java.time.Period.between(getFechaNacimiento(), LocalDate.now()).getYears();
    }
}
