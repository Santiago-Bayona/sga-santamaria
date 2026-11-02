package co.edu.uniquindio.sga.application.dto.response;

/** DTO de salida de la API. Periodo que se repite cada año (mes/día de inicio y de fin). */

public record PeriodoAnualResponse(
        int mesInicio,
        int diaInicio,
        int mesFin,
        int diaFin
) {
}
