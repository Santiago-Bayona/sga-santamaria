package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Nombre, descripción y ubicación exacta del alojamiento (7.3). */

public record ActualizarDatosAlojamientoRequest(

        @NotBlank(message = "El nombre del alojamiento es obligatorio")
        String nombre,

        @NotBlank(message = "La descripción del alojamiento es obligatoria")
        String descripcion,

        @NotBlank(message = "La ciudad es obligatoria")
        String ciudad,

        @NotBlank(message = "La dirección es obligatoria")
        String direccion,

        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90", message = "La latitud mínima es -90")
        @DecimalMax(value = "90", message = "La latitud máxima es 90")
        BigDecimal latitud,

        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180", message = "La longitud mínima es -180")
        @DecimalMax(value = "180", message = "La longitud máxima es 180")
        BigDecimal longitud

) {
}
