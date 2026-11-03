package co.edu.uniquindio.sga.infrastructure.exception;

/**
 * Fallo técnico de la capa de infraestructura (E/S, persistencia, correo, ficheros).
 * No expresa una regla de negocio: indica que el mecanismo externo no pudo completar la operación.
 */
public class InfraestructuraException extends RuntimeException {

    public InfraestructuraException(String mensaje) {
        super(mensaje);
    }

    public InfraestructuraException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
