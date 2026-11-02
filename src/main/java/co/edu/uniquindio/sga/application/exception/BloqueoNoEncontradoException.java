package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;

/**
 * Excepción de aplicación que indica que no existe el bloqueo (se traduce a HTTP 404).
 */
public class BloqueoNoEncontradoException extends RuntimeException {

    public BloqueoNoEncontradoException(CodigoBloqueo codigo) {
        super("No se encontró el bloqueo: " + codigo.valor());
    }
}
