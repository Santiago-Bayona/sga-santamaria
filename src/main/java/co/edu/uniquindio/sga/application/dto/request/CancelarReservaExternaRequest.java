package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CancelarReservaExternaRequest(

        @NotBlank(message = "El código del canal externo es obligatorio")
        String codigoCanal,

        @NotBlank(message = "El identificador externo de la reserva es obligatorio")
        String identificadorExterno

) {
}