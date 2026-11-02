package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;

/**
 * Excepción de aplicación que indica que no se encontró un canal externo.
 */
public class CanalExternoNoEncontradoException extends RuntimeException {

    public CanalExternoNoEncontradoException(CodigoCanalExterno codigo) {
        super("No se encontró el canal externo con código: " + codigo.valor());
    }
}
