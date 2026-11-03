package co.edu.uniquindio.sga.application.exception;

/**
 * Se busca una entidad por su identificador y no existe. La API la traduce a 404, no a 400/409.
 * Vive en la capa de aplicación: el dominio solo conoce {@code ReglaDominioException}.
 */
public class EntidadNoEncontradaException extends RuntimeException {

    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
