package co.edu.uniquindio.sga.application.exception;

/** No se puede dar de baja una cuenta cuyo titular todavía tiene reservas que retienen disponibilidad. */
public class UsuarioConReservasActivasException extends RuntimeException {

    public UsuarioConReservasActivasException(String username) {
        super("No se puede desactivar a " + username + " porque tiene reservas activas.");
    }
}
