package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

/**
 * Excepción de aplicación que indica que una reserva no tiene un folio registrado.
 */

public class FolioNoEncontradoException extends RuntimeException {

    public FolioNoEncontradoException(CodigoReserva codigo) {
        super("No se encontró un folio para la reserva: " + codigo.valor());
    }
}