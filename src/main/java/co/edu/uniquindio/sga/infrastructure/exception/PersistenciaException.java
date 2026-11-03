package co.edu.uniquindio.sga.infrastructure.exception;

/** Error al leer o escribir en H2 / JPA. */
/**
 * La base rechazo la operacion. El manejador HTTP la trata como fallo tecnico, no como regla de negocio.
 */
public class PersistenciaException extends InfraestructuraException {

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
