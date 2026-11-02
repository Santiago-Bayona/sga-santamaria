package co.edu.uniquindio.sga.application.dto.request;

import co.edu.uniquindio.sga.domain.valueobject.GravedadNovedad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarNovedadRequest(

        @NotBlank(message = "El código de la novedad es obligatorio")
        String codigoNovedad,

        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento,

        @NotBlank(message = "La descripción de la novedad es obligatoria")
        String descripcion,

        @NotNull(message = "La gravedad de la novedad es obligatoria")
        GravedadNovedad gravedad

) {
}