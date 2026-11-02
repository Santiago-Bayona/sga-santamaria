package co.edu.uniquindio.sga.application.dto.response;

/** DTO de salida de la API. Resultado de registrar o revertir un pago: el pago y el saldo resultante del folio. */
public record MovimientoPagoResponse(

        String codigoReserva,
        PagoResponse pago,
        long saldo,
        String moneda
) {
}
