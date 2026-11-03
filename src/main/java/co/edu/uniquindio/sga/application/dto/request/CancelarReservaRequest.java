package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CancelarReservaRequest(

        @NotBlank(message = "El motivo de cancelación es obligatorio")
        String motivo

) {
}

