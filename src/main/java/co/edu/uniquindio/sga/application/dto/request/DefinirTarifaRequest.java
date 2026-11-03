package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DefinirTarifaRequest(

        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento,

        @NotBlank(message = "El identificador de la temporada es obligatorio")
        String idTemporada,

        @NotNull(message = "El valor de la tarifa es obligatorio")
        @Positive(message = "La tarifa debe ser mayor que cero")
        BigDecimal valorPorOcupante

) {
}