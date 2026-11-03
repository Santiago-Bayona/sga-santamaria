package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;
import java.util.List;

/** DTO de salida de la API. Calendario de ocupación de un apartamento, noche por noche (7.5). */

public record CalendarioOcupacionResponse(
        String identificacionApartamento,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        List<NocheCalendarioResponse> noches
) {
}
