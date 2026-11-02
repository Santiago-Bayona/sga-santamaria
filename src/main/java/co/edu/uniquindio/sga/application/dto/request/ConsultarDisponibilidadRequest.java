package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Búsqueda de disponibilidad (7.5). Los tres primeros campos son obligatorios; los filtros son todos opcionales.
 * El precio se compara contra el valor estimado de la estancia completa.
 */
public record ConsultarDisponibilidadRequest(

        @NotNull(message = "La fecha de entrada es obligatoria")
        LocalDate fechaEntrada,

        @NotNull(message = "La fecha de salida es obligatoria")
        LocalDate fechaSalida,

        @NotEmpty(message = "Debe indicar la fecha de nacimiento de cada ocupante")
        List<@NotNull(message = "La fecha de nacimiento no puede ser nula")
             @Past(message = "La fecha de nacimiento debe estar en el pasado") LocalDate> fechasNacimiento,

        @Min(value = 1, message = "La capacidad mínima debe ser mayor que cero")
        Integer capacidadMinima,

        @DecimalMin(value = "0", message = "El precio mínimo no puede ser negativo")
        BigDecimal precioMinimo,

        @DecimalMin(value = "0", message = "El precio máximo no puede ser negativo")
        BigDecimal precioMaximo,

        List<@NotBlank(message = "Las características no pueden estar vacías") String> caracteristicas,

        List<@NotBlank(message = "Los servicios no pueden estar vacíos") String> servicios
) {
}
