package co.edu.uniquindio.sga.infrastructure.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SMS simulado: registra destinatario y mensaje. Se reemplaza por un proveedor real.
 */

@Component
public class LoggingSmsAdapter implements SmsNotificacionPort {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingSmsAdapter.class);

    @Override
    public void enviar(String telefono, String mensaje) {
        LOG.info("SMS simulado a {} | {}", telefono, mensaje);
    }
}
