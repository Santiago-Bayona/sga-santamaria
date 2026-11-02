package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;

/**
 * Excepción de aplicación que indica que no se encontró un titular.
 */

public class TitularNoEncontradoException extends RuntimeException {

    public TitularNoEncontradoException(CodigoTitular codigo) {
        super("No se encontró el titular con código: " + codigo.valor());
    }
}