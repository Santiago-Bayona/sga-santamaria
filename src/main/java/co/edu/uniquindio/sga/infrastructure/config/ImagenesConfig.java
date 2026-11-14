package co.edu.uniquindio.sga.infrastructure.config;

import co.edu.uniquindio.sga.domain.port.AlmacenamientoImagenesPort;
import co.edu.uniquindio.sga.infrastructure.filesystem.AlmacenamientoArchivosPort;
import co.edu.uniquindio.sga.infrastructure.filesystem.ImagenesLocalAdapter;
import co.edu.uniquindio.sga.infrastructure.integration.CloudinaryImagenesAdapter;
import co.edu.uniquindio.sga.infrastructure.integration.ImagenesConRespaldoAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Elige dónde se guardan las imágenes según sga.imagenes.proveedor. Cambiar de local a Cloudinary es
 * configuración; con Cloudinary, el almacenamiento local queda como respaldo si el servicio falla (I-02).
 */
@Configuration
@EnableConfigurationProperties(ImagenesProperties.class)
public class ImagenesConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ImagenesConfig.class);

    @Bean
    public AlmacenamientoImagenesPort almacenamientoImagenes(ImagenesProperties imagenes, AlmacenamientoArchivosPort archivos) {
        AlmacenamientoImagenesPort local = new ImagenesLocalAdapter(archivos, imagenes.urlPublica());
        if (!ImagenesProperties.PROVEEDOR_CLOUDINARY.equals(imagenes.proveedor())) {
            return local;
        }
        ImagenesProperties.Cloudinary c = imagenes.cloudinary();
        if (!c.completa()) {
            LOG.warn("sga.imagenes.proveedor=cloudinary pero faltan nube, api-key o api-secret: se usa el almacenamiento local");
            return local;
        }
        LOG.info("Imágenes en Cloudinary (nube {}), con respaldo local", c.nube());
        int espera = c.tiempoEsperaMs() > 0 ? c.tiempoEsperaMs() : 15000;
        return new ImagenesConRespaldoAdapter(
                new CloudinaryImagenesAdapter(c.nube(), c.apiKey(), c.apiSecret(), c.carpeta(), Duration.ofMillis(espera)),
                local);
    }
}
