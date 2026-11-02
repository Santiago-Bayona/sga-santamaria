package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;
import java.util.List;

/** DTO de salida de la API. Reporte de ingresos (7.9): suma de pagos registrados en el periodo, por medio de pago. */

public record ReporteIngresosResponse(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        long totalIngresos,
        String moneda,
        List<IngresoPorMedioResponse> porMedio
) {
}
