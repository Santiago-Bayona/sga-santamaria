package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDateTime;

/** Línea de la bitácora de canales. resultado: ACEPTADA, RECHAZADA_CONFLICTO o DUPLICADO. */
public record EventoBitacoraResponse(
        String codigo,
        String codigoCanal,
        String identificadorExterno,
        LocalDateTime ocurridoEn,
        String payload,
        String resultado,
        String motivo
) {
}
