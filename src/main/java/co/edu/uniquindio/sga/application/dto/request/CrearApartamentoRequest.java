package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Datos para crear un apartamento. Nace inactivo: se activa con ActivarApartamentoUseCase cuando tenga tarifas (7.4). */

public record CrearApartamentoRequest(

        @NotBlank(message = "La identificación del apartamento es obligatoria")
        String identificacionApartamento,

        @NotBlank(message = "El nombre del apartamento es obligatorio")
        String nombre,

        @NotBlank(message = "La descripción del apartamento es obligatoria")
        String descripcion,

        @NotNull(message = "El número de dormitorios es obligatorio")
        @Min(value = 1, message = "El apartamento debe tener al menos un dormitorio")
        Integer dormitorios,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser mayor que cero")
        Integer capacidad,

        @NotEmpty(message = "Debe registrar al menos una imagen")
        @Size(max = 10, message = "Se permiten máximo 10 imágenes")
        List<@NotBlank(message = "Las imágenes no pueden estar vacías") String> imagenes,

        @NotNull(message = "El índice de la imagen principal es obligatorio")
        @Min(value = 0, message = "El índice de la imagen principal no puede ser negativo")
        Integer indiceImagenPrincipal,

        @NotNull(message = "La dotación es obligatoria (puede ir vacía)")
        List<@NotBlank(message = "Los elementos de la dotación no pueden estar vacíos") String> dotacion

) {
}
