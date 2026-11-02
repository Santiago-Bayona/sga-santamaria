package co.edu.uniquindio.sga.application.exception;

import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;

/**
 * Excepción de aplicación que indica que no existe la temporada (se traduce a HTTP 404).
 */
public class TemporadaNoEncontradaException extends RuntimeException {

    public TemporadaNoEncontradaException(IdTemporada id) {
        super("No se encontró la temporada: " + id.valor());
    }
}
