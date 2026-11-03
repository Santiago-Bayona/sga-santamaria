package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Calendario de un apartamento. El rango son NOCHES [fechaDesde, fechaHasta) (3.1). */

public record ConsultarCalendarioOcupacionRequest(

        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento,

        @NotNull(message = "La fecha inicial es obligatoria")
        LocalDate fechaDesde,

        @NotNull(message = "La fecha final es obligatoria")
        LocalDate fechaHasta

) {
}
