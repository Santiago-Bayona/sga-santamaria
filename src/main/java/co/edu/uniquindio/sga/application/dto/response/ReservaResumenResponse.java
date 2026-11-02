package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;

import java.time.LocalDate;

/** DTO de salida de la API (Guía 07). Una fila de un listado de reservas: lo mínimo para reconocerla. */
public record ReservaResumenResponse(

        String codigo,
        EstadoReserva estado,
        String identificacionApartamento,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        String codigoTitular,
        int totalOcupantes,
        long valorTotal                     // pesos, sin decimales (3.4)
) {
}
