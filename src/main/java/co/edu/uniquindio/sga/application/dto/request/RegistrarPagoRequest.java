package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarPagoRequest(

        @NotNull(message = "El monto del pago es obligatorio")
        @Positive(message = "El monto del pago debe ser mayor que cero")
        BigDecimal monto,

        @NotNull(message = "El medio de pago es obligatorio")
        co.edu.uniquindio.sga.domain.valueobject.MedioPago medioPago,

        @NotNull(message = "La fecha del pago es obligatoria")
        LocalDate fecha

) {
}