package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

/** Datos para registrar un titular (responsable de la reserva). */

public record RegistrarTitularRequest(

        @NotBlank(message = "El código del titular es obligatorio")
        String codigoTitular,

        @NotBlank(message = "El nombre del titular es obligatorio")
        String nombre,

        @NotNull(message = "La fecha de nacimiento del titular es obligatoria")
        @Past(message = "La fecha de nacimiento debe estar en el pasado")
        LocalDate fechaNacimiento,

        @NotBlank(message = "El teléfono del titular es obligatorio")
        String telefono,

        @NotBlank(message = "El correo del titular es obligatorio")
        @Email(message = "El correo del titular no tiene un formato válido")
        String correo

) {
}
