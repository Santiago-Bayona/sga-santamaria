package co.edu.uniquindio.sga.infrastructure.integration;

/** Puerto de salida: notificaciones al titular (correo, SMS). */
/**
 * Puerto único de aviso al huésped. La implementación compuesta reparte entre correo y SMS.
 */
public interface NotificacionPort {

    void notificar(String destinatario, String asunto, String mensaje);
}
