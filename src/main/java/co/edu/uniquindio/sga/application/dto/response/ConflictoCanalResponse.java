package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** DTO de salida de la API. Reserva externa rechazada por colisionar con una vigente (RN-18), pendiente de revisión. */
public record ConflictoCanalResponse(

        String codigo,
        CanalOrigen canal,
        String identificadorExterno,
        String identificacionApartamento,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        String codigoReservaVigente,
        LocalDateTime fechaDeteccion,
        boolean revisado,
        String notaRevision
) {
}
