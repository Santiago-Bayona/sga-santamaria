package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Solo se actualiza el contacto: nombre y fecha de nacimiento no cambian (Titular.actualizarContacto). */

public record ActualizarTitularRequest(

        @NotBlank(message = "El código del titular es obligatorio")
        String codigoTitular,

        @NotBlank(message = "El teléfono del titular es obligatorio")
        String telefono,

        @NotBlank(message = "El correo del titular es obligatorio")
        @Email(message = "El correo del titular no tiene un formato válido")
        String correo

) {
}
