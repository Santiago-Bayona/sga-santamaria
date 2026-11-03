package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalDate;

/** DTO de salida de la API. Una noche de la cotización (7.4): fecha, temporada, tarifa, ocupantes facturables y subtotal. */
public record DesgloseNocheResponse(

        LocalDate fecha,
        String temporada,
        long tarifaAplicada,
        int ocupantesFacturables,
        long subtotal
) {
}
