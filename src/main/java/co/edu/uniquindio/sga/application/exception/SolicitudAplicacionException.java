package co.edu.uniquindio.sga.application.exception;

/**
 * Excepción que indica que una solicitud de aplicación
 * no cumple las condiciones necesarias para ejecutarse.
 */

public class SolicitudAplicacionException extends RuntimeException {

    public SolicitudAplicacionException(String mensaje) {
        super(mensaje);
    }
}