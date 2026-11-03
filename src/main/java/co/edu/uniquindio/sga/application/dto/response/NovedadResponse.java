package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.GravedadNovedad;

import java.time.LocalDateTime;

/** DTO de salida de la API. Una novedad sobre un apartamento (7.6). */
public record NovedadResponse(

        String codigo,
        String identificacionApartamento,
        LocalDateTime fecha,
        String autor,
        String descripcion,
        GravedadNovedad gravedad,
        boolean resuelta,
        LocalDateTime fechaResolucion
) {
}
