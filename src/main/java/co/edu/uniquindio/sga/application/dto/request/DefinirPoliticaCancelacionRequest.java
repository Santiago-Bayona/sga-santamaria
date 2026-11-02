package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Contenido de la NUEVA versión de la política de cancelación (RN-13). La versión la calcula el dominio. */

public record DefinirPoliticaCancelacionRequest(

        @NotNull(message = "Los tramos de la política son obligatorios")
        @Size(min = 2, message = "La política requiere al menos dos tramos")
        List<@NotNull @Valid TramoCancelacionRequest> tramos,

        @NotNull(message = "La retención por no-show es obligatoria")
        @Min(value = 0, message = "La retención por no-show no puede ser menor que 0")
        @Max(value = 100, message = "La retención por no-show no puede ser mayor que 100")
        Integer retencionNoShow

) {
}
