package co.edu.uniquindio.sga.infrastructure.exception;

/** Fallo de un adaptador hacia el exterior (correo, sistema de ficheros, API de canal). */
/**
 * Fallo al hablar con disco, correo u otro sistema externo.
 */
public class IntegracionExternaException extends InfraestructuraException {

    public IntegracionExternaException(String mensaje) {
        super(mensaje);
    }

    public IntegracionExternaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
