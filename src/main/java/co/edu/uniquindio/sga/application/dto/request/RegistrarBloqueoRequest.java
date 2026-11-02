package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegistrarBloqueoRequest(

        @NotBlank(message = "El código del bloqueo es obligatorio")
        String codigoBloqueo,

        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento,

        @NotNull(message = "La fecha de entrada es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDate fechaFin,

        @NotBlank(message = "El motivo del bloqueo es obligatorio")
        String motivo

) {
}