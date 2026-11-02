package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;
import java.util.List;

/** DTO de salida de la API. Reporte de reservas por canal de origen (7.9), según su fecha de creación. */

public record ReporteReservasPorCanalResponse(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        List<ReservasPorCanalResponse> canales
) {
}
