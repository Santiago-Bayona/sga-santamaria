package co.edu.uniquindio.sga.application.dto.response;

/** DTO de salida de la API. Resultado de registrar un servicio adicional o revertir un cargo: el cargo y el saldo resultante. */
public record MovimientoCargoResponse(

        String codigoReserva,
        CargoResponse cargo,
        long saldo,
        String moneda
) {
}
