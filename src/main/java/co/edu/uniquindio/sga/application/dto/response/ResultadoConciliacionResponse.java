package co.edu.uniquindio.sga.application.dto.response;

/**
 * DTO de salida de la API. Respuesta al canal externo: tipo = ACEPTADA, DUPLICADA (mismo mensaje dos veces, RN-19)
 * o CONFLICTO (RN-18). reserva viene en ACEPTADA y DUPLICADA; conflicto solo en CONFLICTO. El otro va null.
 */
public record ResultadoConciliacionResponse(

        String tipo,
        ReservaDetalleResponse reserva,
        ConflictoCanalResponse conflicto
) {
}
