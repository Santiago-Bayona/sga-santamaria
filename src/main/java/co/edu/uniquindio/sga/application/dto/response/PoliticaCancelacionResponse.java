package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/** DTO de salida de la API. Una versión de la política de cancelación (RN-13). */
public record PoliticaCancelacionResponse(
        String codigo,
        int version,
        boolean vigente,
        List<TramoCancelacionResponse> tramos,   // de mayor a menor antelación
        int retencionNoShow,
        LocalDateTime creadaEn
) {
}
