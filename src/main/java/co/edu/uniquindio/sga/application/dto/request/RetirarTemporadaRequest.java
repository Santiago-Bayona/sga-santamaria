package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RetirarTemporadaRequest(

        @NotBlank(message = "El identificador de la temporada es obligatorio")
        String idTemporada

) {
}