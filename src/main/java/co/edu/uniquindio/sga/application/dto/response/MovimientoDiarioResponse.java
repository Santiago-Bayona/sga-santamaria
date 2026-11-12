package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.MovimientoDiario;

import java.time.LocalTime;

/** DTO de salida (Guía 07): una llegada o una salida prevista dentro del resumen diario. */
public record MovimientoDiarioResponse(
        String codigoReserva,
        String identificacionApartamento,
        LocalTime horaEstimada,             // nula en las salidas
        int totalOcupantes
) {

    public static MovimientoDiarioResponse de(MovimientoDiario movimiento) {
        return new MovimientoDiarioResponse(movimiento.reserva().valor(), movimiento.apartamento().valor(),
                movimiento.horaEstimada(), movimiento.totalOcupantes());
    }
}
