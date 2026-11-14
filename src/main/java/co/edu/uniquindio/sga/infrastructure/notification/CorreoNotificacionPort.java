package co.edu.uniquindio.sga.infrastructure.notification;

/**
 * Puerto de salida técnico: envío de correo. El dominio solo marca el recordatorio ENVIADO/FALLIDO;
 * el mecanismo (Brevo o su simulación en el log) vive aquí y se elige por configuración en {@link CorreoConfig}.
 */
public interface CorreoNotificacionPort {

    void enviar(String destinatario, String asunto, String cuerpo);

    /**
     * Correo con versión HTML (botones, enlaces). El texto plano es la alternativa para clientes sin HTML.
     * Por defecto cae al envío de texto, así un adaptador simple no tiene que implementarlo.
     */
    default void enviarHtml(String destinatario, String asunto, String texto, String html) {
        enviar(destinatario, asunto, texto);
    }
}
