package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.MedioPago;

/** DTO de salida de la API. Ingresos de un medio de pago en el periodo, en pesos. */

public record IngresoPorMedioResponse(
        MedioPago medio,
        long total
) {
}
