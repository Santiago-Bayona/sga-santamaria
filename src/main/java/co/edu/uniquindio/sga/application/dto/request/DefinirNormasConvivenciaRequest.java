package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Normas de convivencia del alojamiento: reemplazan a las anteriores. */

public record DefinirNormasConvivenciaRequest(

        @NotNull(message = "La lista de normas es obligatoria (puede ir vacía)")
        List<@NotBlank(message = "Las normas no pueden estar vacías") String> normas

) {
}
