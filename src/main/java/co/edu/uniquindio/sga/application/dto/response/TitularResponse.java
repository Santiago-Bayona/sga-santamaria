package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;

/** DTO de salida de la API. Datos del titular (responsable de la reserva). */

public record TitularResponse(
        String codigo,
        String nombre,
        LocalDate fechaNacimiento,
        String telefono,
        String correo
) {
}
