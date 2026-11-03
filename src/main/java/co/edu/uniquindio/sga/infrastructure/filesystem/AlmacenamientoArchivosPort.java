package co.edu.uniquindio.sga.infrastructure.filesystem;

/**
 * Puerto técnico para el sistema de ficheros (galería de apartamentos, 7.3).
 * El dominio solo guarda URLs; la infraestructura decide si son locales o de un servicio externo.
 */
public interface AlmacenamientoArchivosPort {

    String guardar(String nombreLogico, byte[] contenido);

    byte[] leer(String rutaRelativa);
}
