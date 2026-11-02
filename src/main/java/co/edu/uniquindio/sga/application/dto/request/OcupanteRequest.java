package co.edu.uniquindio.sga.application.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record OcupanteRequest(

        @NotBlank(message = "El nombre del ocupante es obligatorio")
        String nombre,

        @NotNull(message = "La fecha de nacimiento del ocupante es obligatoria")
        LocalDate fechaNacimiento

) {
    }

