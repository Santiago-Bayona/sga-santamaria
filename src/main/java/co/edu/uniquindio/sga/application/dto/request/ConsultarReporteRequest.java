package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Rango de fechas de los reportes (7.9). Ambas fechas son inclusivas: del 1 al 31 de octubre son 31 días. */

public record ConsultarReporteRequest(

        @NotNull(message = "La fecha inicial es obligatoria")
        LocalDate fechaDesde,

        @NotNull(message = "La fecha final es obligatoria")
        LocalDate fechaHasta

) {
}
