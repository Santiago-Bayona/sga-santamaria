package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record IdentificacionApartamentoRequest(

        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento

) {
}