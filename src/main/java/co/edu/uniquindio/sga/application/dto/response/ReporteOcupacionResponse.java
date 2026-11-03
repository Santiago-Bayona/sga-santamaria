package co.edu.uniquindio.sga.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de salida de la API. Reporte de ocupación (7.9).
 * porcentajeOcupacion = nochesVendidas / (apartamentosActivos x nochesPeriodo) x 100, con dos decimales.
 */
public record ReporteOcupacionResponse(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        long nochesVendidas,
        int apartamentosActivos,
        long nochesPeriodo,
        BigDecimal porcentajeOcupacion
) {
}
