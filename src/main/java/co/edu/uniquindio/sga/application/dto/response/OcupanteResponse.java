package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;

/** DTO de salida de la API (Guía 07). Un ocupante de la reserva: del ocupante solo se conoce nombre y fecha de nacimiento. */
public record OcupanteResponse(

        String nombre,
        LocalDate fechaNacimiento
) {
}
