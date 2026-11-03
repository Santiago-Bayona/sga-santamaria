package co.edu.uniquindio.sga.infrastructure.notification;

/**
 * Puerto de salida técnico: envío de correo. El dominio solo marca el recordatorio ENVIADO/FALLIDO;
 * el mecanismo SMTP (o su simulación) vive aquí.
 */
public interface CorreoNotificacionPort {

    void enviar(String destinatario, String asunto, String cuerpo);
}
