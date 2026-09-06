package main.java.com.ingsoft.vitaligold.dto.response;

/**
 * Datos del usuario que devuelve la base de datos al buscarlo
 * por su correo electrónico.
 */
public class LoginResponse {

    private String nombre;
    private String apellido;
    private String contrasenaHash;
    private String rol;

    public LoginResponse(String nombre, String apellido, String contrasenaHash, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.contrasenaHash = contrasenaHash;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
