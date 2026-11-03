package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Servicio que ofrece el alojamiento (7.3). No es lo mismo que  RegistrarServicioAdicionalRequest, que lo carga a un folio. */

public record AgregarServicioAdicionalAlojamientoRequest(

        @NotBlank(message = "El nombre del servicio es obligatorio")
        String nombre,

        @NotNull(message = "El valor del servicio es obligatorio")
        @Min(value = 0, message = "El valor del servicio no puede ser negativo")
        Long valor,                    // pesos, sin decimales (3.4)

        @NotNull(message = "Debe indicar si el servicio genera cargo")
        Boolean generaCargo

) {
}
