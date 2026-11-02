package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CrearTemporadaRequest(

        @NotBlank(message = "El identificador de la temporada es obligatorio")
        String idTemporada,

        @NotBlank(message = "El nombre de la temporada es obligatorio")
        String nombre,

        @NotNull(message = "Debe indicar si es una temporada base")
        Boolean base,

        @NotNull(message = "La lista de periodos es obligatoria")
        List<@Valid PeriodoAnualRequest> periodos,

        @NotNull(message = "La lista de tarifas es obligatoria")
        List<@Valid TarifaApartamentoRequest> tarifas

) {
}