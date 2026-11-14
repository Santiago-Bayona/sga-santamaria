package co.edu.uniquindio.sga.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adaptador de correo para desarrollo: registra el mensaje en el log (sga.correo.proveedor=log).
 * Lo crea {@link CorreoConfig}; se reemplaza por {@link BrevoCorreoAdapter} sin tocar dominio ni aplicación.
 */
public class LoggingCorreoAdapter implements CorreoNotificacionPort {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingCorreoAdapter.class);

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        LOG.info("Correo simulado a {} | {} | {}", destinatario, asunto, cuerpo);
    }
}
