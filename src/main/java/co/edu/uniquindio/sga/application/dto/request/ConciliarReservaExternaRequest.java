package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ConciliarReservaExternaRequest(

        @NotBlank(message = "El código del canal es obligatorio")
        String codigoCanal,

        @NotBlank(message = "El identificador externo es obligatorio")
        String identificadorExterno,

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
        LocalTime horaLlegadaEstimada

) {
}