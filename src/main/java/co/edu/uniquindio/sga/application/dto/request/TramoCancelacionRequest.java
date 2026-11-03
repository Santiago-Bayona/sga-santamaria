package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Un tramo de la política: desde X días de antelación mínima se retiene Y% del anticipo. */

public record TramoCancelacionRequest(

        @NotNull(message = "Los días de antelación mínima son obligatorios")
        @Min(value = 0, message = "Los días de antelación no pueden ser negativos")
        Integer diasAntelacionMinima,

        @NotNull(message = "El porcentaje de retención es obligatorio")
        @Min(value = 0, message = "El porcentaje de retención no puede ser menor que 0")
        @Max(value = 100, message = "El porcentaje de retención no puede ser mayor que 100")
        Integer porcentajeRetencion

) {
}
