package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

/**
 * Excepción de aplicación que indica que ya existe un apartamento con esa identificación.
 */

public class ApartamentoYaRegistradoException extends RuntimeException {

    public ApartamentoYaRegistradoException(IdentificacionApartamento identificacion) {
        super("Ya existe un apartamento con identificación: " + identificacion.valor());
    }
}
