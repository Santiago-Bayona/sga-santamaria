package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;

import java.time.LocalDate;

/** DTO de salida de la API. Una noche del calendario. codigoReserva/estadoReserva solo si OCUPADA; motivoBloqueo solo si BLOQUEADA. */
public record NocheCalendarioResponse(
        LocalDate fecha,
        SituacionNoche situacion,
        String codigoReserva,
        EstadoReserva estadoReserva,
        String motivoBloqueo
) {
}
