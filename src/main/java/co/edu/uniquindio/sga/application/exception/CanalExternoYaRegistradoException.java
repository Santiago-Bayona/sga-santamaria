package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;

/**
 * Excepción de aplicación que indica que ya existe un canal externo con ese código.
 */

public class CanalExternoYaRegistradoException extends RuntimeException {

    public CanalExternoYaRegistradoException(CodigoCanalExterno codigo) {
        super("Ya existe un canal externo con código: " + codigo.valor());
    }
}
