package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record RevertirPagoRequest(

        @NotNull(message = "El número del pago es obligatorio")
        @Positive(message = "El número del pago debe ser mayor que cero")
        Integer numeroPago,

        @NotNull(message = "La fecha de reversión es obligatoria")
        LocalDate fecha

) {}
