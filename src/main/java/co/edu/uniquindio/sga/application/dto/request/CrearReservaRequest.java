package co.edu.uniquindio.sga.application.dto.request;

import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CrearReservaRequest(

        @NotBlank(message = "El código del titular es obligatorio")
        String codigoTitular,

        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento,

        @NotNull(message = "La fecha de entrada es obligatoria")
        LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDate fechaSalida,

        @NotNull(message = "La lista de acompañantes es obligatoria (puede ir vacía)")
        List<@NotNull @Valid OcupanteRequest> acompanantes,

        @NotNull(message = "La hora estimada de llegada es obligatoria")
        LocalTime horaLlegadaEstimada,

        @NotNull(message = "El canal de origen es obligatorio")
        CanalOrigen canalOrigen,

        String identificadorExterno,

        /** Mascotas que acompañan la estancia (L-16); null equivale a 0. Máximo 2 por apartamento. */
        @Min(value = 0, message = "La cantidad de mascotas no puede ser negativa")
        Integer mascotas

) {
}
