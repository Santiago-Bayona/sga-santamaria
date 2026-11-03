package co.edu.uniquindio.sga.infrastructure.integration;
/**
 * Puerto de SMS. La implementacion actual escribe en el log.
 */

public interface SmsNotificacionPort {

    void enviar(String telefono, String mensaje);
}
