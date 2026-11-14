package co.edu.uniquindio.sga.infrastructure.security;

/** El enlace de recuperación no existe, ya se usó o venció. Responde 400 sin decir cuál de las tres. */
public class TokenRecuperacionInvalidoException extends RuntimeException {

    public TokenRecuperacionInvalidoException() {
        super("El enlace no es válido o ya venció. Solicita uno nuevo.");
    }
}
