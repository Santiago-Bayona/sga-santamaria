package co.edu.uniquindio.sga.infrastructure.filesystem;

import co.edu.uniquindio.sga.domain.port.AlmacenamientoImagenesPort;

/**
 * Respaldo local de la galería (I-02): guarda la imagen en sga.archivos.directorio y la publica en
 * {@code {url-publica}/api/archivos/imagenes/{nombre}} (ArchivoRestController). Sirve sin cuentas ni red.
 */
public class ImagenesLocalAdapter implements AlmacenamientoImagenesPort {

    public static final String RUTA_PUBLICA = "/api/archivos/imagenes/";

    private final AlmacenamientoArchivosPort archivos;
    private final String urlPublica;

    public ImagenesLocalAdapter(AlmacenamientoArchivosPort archivos, String urlPublica) {
        this.archivos = archivos;
        String base = urlPublica == null ? "" : urlPublica.trim();
        this.urlPublica = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    @Override
    public String guardar(String nombre, String tipoContenido, byte[] contenido) {
        String ruta = archivos.guardar(nombre, contenido);
        return urlPublica + RUTA_PUBLICA + ruta;
    }
}
