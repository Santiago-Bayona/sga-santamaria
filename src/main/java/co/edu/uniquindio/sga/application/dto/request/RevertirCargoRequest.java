package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record RevertirCargoRequest(

        @NotNull(message = "El número del cargo es obligatorio")
        @Positive(message = "El número del cargo debe ser mayor que cero")
        Integer numeroCargo,

        @NotNull(message = "La fecha de reversión es obligatoria")
        LocalDate fecha

) {}