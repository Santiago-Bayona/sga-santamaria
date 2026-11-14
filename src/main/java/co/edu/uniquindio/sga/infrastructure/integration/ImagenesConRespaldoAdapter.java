package co.edu.uniquindio.sga.infrastructure.integration;

import co.edu.uniquindio.sga.domain.port.AlmacenamientoImagenesPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usa el servicio externo y, si falla, guarda la imagen en el respaldo local (I-02): subir una foto nunca queda
 * bloqueado porque Cloudinary no responda.
 */
public class ImagenesConRespaldoAdapter implements AlmacenamientoImagenesPort {

    private static final Logger LOG = LoggerFactory.getLogger(ImagenesConRespaldoAdapter.class);

    private final AlmacenamientoImagenesPort principal;
    private final AlmacenamientoImagenesPort respaldo;

    public ImagenesConRespaldoAdapter(AlmacenamientoImagenesPort principal, AlmacenamientoImagenesPort respaldo) {
        this.principal = principal;
        this.respaldo = respaldo;
    }

    @Override
    public String guardar(String nombre, String tipoContenido, byte[] contenido) {
        try {
            return principal.guardar(nombre, tipoContenido, contenido);
        } catch (RuntimeException e) {
            LOG.warn("El servicio de imágenes falló ({}); se guarda {} en el respaldo local", e.getMessage(), nombre);
            return respaldo.guardar(nombre, tipoContenido, contenido);
        }
    }
}
