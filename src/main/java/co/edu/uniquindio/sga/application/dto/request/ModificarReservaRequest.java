package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ModificarReservaRequest(

        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento,

        @NotNull(message = "La fecha de entrada es obligatoria")
        LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDate fechaSalida,

        @NotNull(message = "La lista de acompañantes es obligatoria (puede ir vacía)")
        List<@NotNull @Valid OcupanteRequest> acompanantes,

        /** Mascotas de la nueva estancia (L-16, máximo 2); null conserva las que ya tenía la reserva. */
        @Min(value = 0, message = "La cantidad de mascotas no puede ser negativa")
        Integer mascotas

) {
}

