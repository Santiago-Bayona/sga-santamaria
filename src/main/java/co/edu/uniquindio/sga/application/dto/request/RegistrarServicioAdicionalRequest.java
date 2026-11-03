package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegistrarServicioAdicionalRequest(

        @NotBlank(message = "El nombre del servicio adicional es obligatorio")
        String nombreServicio,

        @NotNull(message = "La fecha del cargo es obligatoria")
        LocalDate fecha

) {}