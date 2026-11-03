package co.edu.uniquindio.sga.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** DTO de salida de la API. Ficha del alojamiento (7.3). */

public record AlojamientoResponse(
        String codigo,
        String nombre,
        String descripcion,
        String ciudad,
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud,
        ConfiguracionAlojamientoResponse configuracion,
        List<String> normasConvivencia,
        List<ServicioAdicionalResponse> serviciosAdicionales
) {
}
