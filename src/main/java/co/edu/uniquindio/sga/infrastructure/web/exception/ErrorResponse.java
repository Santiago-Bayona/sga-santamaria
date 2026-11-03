package co.edu.uniquindio.sga.infrastructure.web.exception;

import java.time.LocalDateTime;
import java.util.List;

/** Cuerpo uniforme de error de la API. */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        List<String> detalles
) {
}
