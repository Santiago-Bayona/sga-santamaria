package co.edu.uniquindio.sga.application.dto.response;

/** DTO de salida de la API. Resultado de modificar una reserva: el ajuste registrado en el folio (RN-14). */
public record ModificacionReservaResponse(

        String codigoReserva,
        long ajuste,        // positivo: el huésped debe más; negativo: queda a su favor; 0: sin diferencia
        String moneda
) {
}
