package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PeriodoAnualRequest(

        @NotNull(message = "El mes inicial es obligatorio")
        @Min(1)
        @Max(12)
        Integer mesInicio,

        @NotNull(message = "El día inicial es obligatorio")
        @Min(1)
        @Max(31)
        Integer diaInicio,

        @NotNull(message = "El mes final es obligatorio")
        @Min(1)
        @Max(12)
        Integer mesFin,

        @NotNull(message = "El día final es obligatorio")
        @Min(1)
        @Max(31)
        Integer diaFin

) {
}