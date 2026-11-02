package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;

/**
 * Excepción de aplicación que indica que ya existe un titular con ese código.
 */

public class TitularYaRegistradoException extends RuntimeException {

    public TitularYaRegistradoException(CodigoTitular codigo) {
        super("Ya existe un titular con código: " + codigo.valor());
    }
}
