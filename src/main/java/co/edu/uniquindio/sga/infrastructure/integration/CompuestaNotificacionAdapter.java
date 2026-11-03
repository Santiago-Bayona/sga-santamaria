package co.edu.uniquindio.sga.infrastructure.integration;

import co.edu.uniquindio.sga.infrastructure.notification.CorreoNotificacionPort;
import org.springframework.stereotype.Component;

/**
 * Envia el aviso por correo y, si el destinatario empieza por +, también por SMS.
 */

@Component
public class CompuestaNotificacionAdapter implements NotificacionPort {

    private final CorreoNotificacionPort correo;
    private final SmsNotificacionPort sms;

    public CompuestaNotificacionAdapter(CorreoNotificacionPort correo, SmsNotificacionPort sms) {
        this.correo = correo;
        this.sms = sms;
    }

    @Override
    public void notificar(String destinatario, String asunto, String mensaje) {
        correo.enviar(destinatario, asunto, mensaje);
        if (destinatario != null && destinatario.startsWith("+")) {
            sms.enviar(destinatario, asunto + " — " + mensaje);
        }
    }
}
