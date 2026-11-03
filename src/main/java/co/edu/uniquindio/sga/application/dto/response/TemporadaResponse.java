package co.edu.uniquindio.sga.application.dto.response;

import java.util.List;

/** DTO de salida de la API. Temporada del alojamiento. La base cubre todas las fechas no asignadas a otra (F-05). */

public record TemporadaResponse(
        String id,
        String nombre,
        boolean base,
        List<PeriodoAnualResponse> periodos
) {
}
