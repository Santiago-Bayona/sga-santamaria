package co.edu.uniquindio.sga.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Galería de apartamentos (7.3, I-02). Las credenciales de Cloudinary van en application-secrets.yml o en
 * variables de entorno, nunca versionadas.
 *
 * @param proveedor  {@code local} (por defecto) o {@code cloudinary}; con cloudinary, el local queda de respaldo
 * @param urlPublica URL del backend con la que se arman las direcciones de las imágenes locales
 */
@ConfigurationProperties(prefix = "sga.imagenes")
public record ImagenesProperties(String proveedor, String urlPublica, Cloudinary cloudinary) {

    public static final String PROVEEDOR_LOCAL = "local";
    public static final String PROVEEDOR_CLOUDINARY = "cloudinary";

    public ImagenesProperties {
        proveedor = proveedor == null || proveedor.isBlank() ? PROVEEDOR_LOCAL : proveedor.trim().toLowerCase();
        urlPublica = urlPublica == null || urlPublica.isBlank() ? "http://localhost:8080" : urlPublica.trim();
        cloudinary = cloudinary == null ? new Cloudinary(null, null, null, null, 0) : cloudinary;
    }

    public record Cloudinary(String nube, String apiKey, String apiSecret, String carpeta, int tiempoEsperaMs) {

        public boolean completa() {
            return nube != null && !nube.isBlank() && apiKey != null && !apiKey.isBlank()
                    && apiSecret != null && !apiSecret.isBlank();
        }
    }
}
