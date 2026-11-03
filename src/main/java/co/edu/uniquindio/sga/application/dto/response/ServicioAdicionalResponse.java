package co.edu.uniquindio.sga.application.dto.response;

/** DTO de salida de la API. Servicio adicional del alojamiento (7.3). */

public record ServicioAdicionalResponse(
        String nombre,
        long valor,            // pesos
        boolean generaCargo
) {
}
