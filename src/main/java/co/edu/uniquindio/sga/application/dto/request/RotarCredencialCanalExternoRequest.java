package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RotarCredencialCanalExternoRequest(

        @NotBlank(message = "El código del canal es obligatorio")
        String codigoCanal,

        @NotBlank(message = "La nueva credencial es obligatoria")
        String nuevaCredencial

) {
}
