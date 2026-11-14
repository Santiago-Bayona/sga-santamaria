package co.edu.uniquindio.sga.domain.port;

/**
 * Puerto de salida (I-01) para la galería de los apartamentos (7.3): guarda una imagen en un servicio externo y
 * devuelve su URL pública, que es lo único que guarda el dominio. Hay una implementación externa (Cloudinary) y
 * una local de respaldo (I-02); cuál se usa es configuración.
 *
 * Si la imagen no se pudo guardar, las implementaciones lanzan una excepción no verificada.
 */
public interface AlmacenamientoImagenesPort {

    String guardar(String nombre, String tipoContenido, byte[] contenido);
}
