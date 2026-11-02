package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** DTO de salida de la API. Un bloqueo: decisión administrativa con rango y motivo (3.3). */
public record BloqueoResponse(

        String codigo,
        String identificacionApartamento,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String motivo,
        LocalDateTime fechaRegistro,
        boolean vigente
) {
}
