package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReprogramarBloqueoRequest(

        @NotNull(message = "La nueva fecha de entrada es obligatoria")
        LocalDate nuevaFechaInicio,

        @NotNull(message = "La nueva fecha de salida es obligatoria")
        LocalDate nuevaFechaFin

) {
}