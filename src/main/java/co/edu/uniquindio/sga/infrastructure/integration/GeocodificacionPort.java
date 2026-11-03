package co.edu.uniquindio.sga.infrastructure.integration;

import java.math.BigDecimal;

/** Cliente de mapas / geocodificación (reemplazable por Google Maps, Nominatim, etc.). */
/**
 * Puerto de mapas. Hoy responde coordenadas fijas de Santamaría.
 */
public interface GeocodificacionPort {

    Coordenadas geocodificar(String direccion);

    record Coordenadas(BigDecimal latitud, BigDecimal longitud) {
    }
}
