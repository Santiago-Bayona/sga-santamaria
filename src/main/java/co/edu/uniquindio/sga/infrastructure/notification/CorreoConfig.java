package co.edu.uniquindio.sga.infrastructure.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Elige el adaptador del puerto de correo según {@code sga.correo.proveedor}: cambiar de simulado a Brevo es
 * configuración, no código. Lo usan los recordatorios (RN-23) y la recuperación de contraseña.
 */
@Configuration
@EnableConfigurationProperties(CorreoProperties.class)
public class CorreoConfig {

    private static final Logger LOG = LoggerFactory.getLogger(CorreoConfig.class);

    @Bean
    public CorreoNotificacionPort correoNotificacion(CorreoProperties correo) {
        if (CorreoProperties.PROVEEDOR_BREVO.equals(correo.proveedor())) {
            LOG.info("Correo real con Brevo, remitente {}", correo.remitente());
            return new BrevoCorreoAdapter(correo.brevoClave(), correo.remitente(), correo.nombreRemitente(),
                    Duration.ofMillis(correo.tiempoEsperaMs()));
        }
        if (!CorreoProperties.PROVEEDOR_LOG.equals(correo.proveedor())) {
            LOG.warn("Proveedor de correo desconocido '{}': se usa el correo simulado en el log", correo.proveedor());
        }
        return new LoggingCorreoAdapter();
    }
}
