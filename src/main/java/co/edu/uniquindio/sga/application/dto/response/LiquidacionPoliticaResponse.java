package co.edu.uniquindio.sga.application.dto.response;

/**
 * DTO de salida de la API. Resultado de aplicar la política CONGELADA (RN-13) al cancelar o declarar no-show.
 * saldoFolio negativo = saldo a favor del huésped; el desembolso ocurre fuera del sistema (3.4).
 */
public record LiquidacionPoliticaResponse(

        String codigoReserva,
        long retencion,
        long saldoFolio,
        String moneda
) {
}
