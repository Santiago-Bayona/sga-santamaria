package co.edu.uniquindio.sga.application.dto.response;

import java.util.List;

/** DTO de salida de la API. Cotización de una estancia con su desglose noche por noche (RN-05). */
public record CotizacionResponse(

        int noches,
        long total,
        String moneda,
        List<DesgloseNocheResponse> desglose
) {
}
