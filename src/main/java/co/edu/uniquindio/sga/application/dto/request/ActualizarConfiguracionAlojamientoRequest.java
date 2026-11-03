package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/**
 * Parámetros configurables del alojamiento (7.3 y sección 6).
 */

public record ActualizarConfiguracionAlojamientoRequest(

        @NotNull(message = "La hora de entrada es obligatoria")
        LocalTime horaEntrada,

        @NotNull(message = "La hora de salida es obligatoria")
        LocalTime horaSalida,

        @NotNull(message = "El tiempo de preparación es obligatorio")
        @Min(value = 0, message = "El tiempo de preparación no puede ser negativo")
        Integer tiempoPreparacionMinutos,        // minutos: la Ficha pide 2 h 30 min (150), que en horas enteras no cabe

        @NotNull(message = "El plazo de confirmación es obligatorio")
        @Min(value = 1, message = "El plazo de confirmación debe ser de al menos una hora")
        Integer plazoConfirmacionHoras,

        @NotNull(message = "La hora límite de no-show es obligatoria")
        LocalTime horaLimiteNoShow,

        @NotNull(message = "El umbral de edad facturable es obligatorio")
        @Min(value = 0, message = "El umbral de edad no puede ser negativo")
        Integer umbralEdadFacturable,

        @NotNull(message = "El porcentaje de anticipo es obligatorio")
        @Min(value = 0, message = "El porcentaje de anticipo no puede ser menor que 0")
        @Max(value = 100, message = "El porcentaje de anticipo no puede ser mayor que 100")
        Integer porcentajeAnticipo,

        @NotNull(message = "La antelación del recordatorio de entrada es obligatoria")
        @Min(value = 1, message = "La antelación del recordatorio de entrada debe ser de al menos una hora")
        Integer antelacionRecordatorioEntradaHoras,

        @NotNull(message = "La antelación del recordatorio de salida es obligatoria")
        @Min(value = 1, message = "La antelación del recordatorio de salida debe ser de al menos una hora")
        Integer antelacionRecordatorioSalidaHoras

) {
}
