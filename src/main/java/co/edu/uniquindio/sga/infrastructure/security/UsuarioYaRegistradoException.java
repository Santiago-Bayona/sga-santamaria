package co.edu.uniquindio.sga.infrastructure.security;

/** Registro público (7.2): el correo es único. Se traduce a 409 en el manejador global. */
public class UsuarioYaRegistradoException extends RuntimeException {

    public UsuarioYaRegistradoException(String correo) {
        super("Ya existe un usuario registrado con el correo " + correo + ".");
    }
}
