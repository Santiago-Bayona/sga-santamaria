package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CambiarCapacidadApartamentoRequest(

        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento,

        @NotNull(message = "La nueva capacidad es obligatoria")
        @Positive(message = "La capacidad debe ser mayor que cero")
        Integer nuevaCapacidad

) {
}