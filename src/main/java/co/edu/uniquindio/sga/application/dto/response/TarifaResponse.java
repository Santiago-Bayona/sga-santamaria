package co.edu.uniquindio.sga.application.dto.response;

/** DTO de salida de la API. Tarifa = valor por ocupante facturable, por noche, en una temporada (7.4). */

public record TarifaResponse(
        String idTemporada,
        long valorPorOcupante      // pesos, sin decimales
) {
}
