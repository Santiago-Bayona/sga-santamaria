package co.edu.uniquindio.sga.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador de correo para desarrollo: registra el mensaje en el log.
 * Se puede reemplazar por un cliente SMTP sin tocar dominio ni aplicación.
 */
@Component
public class LoggingCorreoAdapter implements CorreoNotificacionPort {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingCorreoAdapter.class);

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        LOG.info("Correo simulado a {} | {} | {}", destinatario, asunto, cuerpo);
    }
}
