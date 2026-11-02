package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** DTO de salida de la API (Guía 07). Detalle completo de una reserva. */
public record ReservaDetalleResponse(
        String codigo,                      // "RES-2026-00001", no el objeto CodigoReserva
        EstadoReserva estado,
        CanalOrigen canalOrigen,
        String identificadorExterno,        // solo en reservas de canal externo (RN-19)
        String codigoTitular,
        String identificacionApartamento,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        int noches,
        LocalTime horaEstimadaLlegada,      // null hasta que se registre (RN-09)
        List<OcupanteResponse> ocupantes,   // todos, el titular incluido
        int totalOcupantes,
        long valorTotal,                    // pesos, sin decimales (3.4); el valor congelado (RN-22)
        String moneda,                      // "COP"
        int versionPoliticaCancelacion,     // la congelada en la reserva (RN-13, RN-22)
        LocalDateTime fechaCreacion,
        LocalDateTime momentoRegistro,      // null hasta la llegada
        LocalDateTime momentoSalida,        // null hasta la salida
        String motivoCancelacion,           // solo si la reserva fue cancelada
        int mascotas                        // L-16: 0, 1 o 2
) {
}
