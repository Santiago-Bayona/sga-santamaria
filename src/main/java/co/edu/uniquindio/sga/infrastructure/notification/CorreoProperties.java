package co.edu.uniquindio.sga.infrastructure.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración del envío de correo. Todo vive en application.yml; la clave de Brevo, en application-secrets.yml
 * o en la variable de entorno SGA_BREVO_CLAVE (nunca versionada).
 *
 * @param proveedor       {@code log} (simulado, por defecto) o {@code brevo}
 * @param remitente       correo verificado como remitente en Brevo (puede ser un Gmail)
 * @param nombreRemitente nombre que ve el destinatario
 * @param brevoClave      API key de Brevo (xkeysib-...)
 * @param tiempoEsperaMs  tiempo máximo de la llamada a Brevo
 */
@ConfigurationProperties(prefix = "sga.correo")
public record CorreoProperties(
        String proveedor,
        String remitente,
        String nombreRemitente,
        String brevoClave,
        int tiempoEsperaMs
) {

    public static final String PROVEEDOR_LOG = "log";
    public static final String PROVEEDOR_BREVO = "brevo";

    public CorreoProperties {
        proveedor = (proveedor == null || proveedor.isBlank()) ? PROVEEDOR_LOG : proveedor.trim().toLowerCase();
        nombreRemitente = (nombreRemitente == null || nombreRemitente.isBlank()) ? "Santamaría" : nombreRemitente.trim();
        tiempoEsperaMs = tiempoEsperaMs <= 0 ? 8000 : tiempoEsperaMs;
    }
}
