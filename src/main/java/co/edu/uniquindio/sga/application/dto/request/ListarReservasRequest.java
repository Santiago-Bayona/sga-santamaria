package co.edu.uniquindio.sga.application.dto.request;

import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;

import java.time.LocalDate;

/**
 * Filtros del listado de reservas (7.5).
 */

public record ListarReservasRequest(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        EstadoReserva estado,
        String identificacionApartamento,
        CanalOrigen canalOrigen,
        String codigoTitular
) {
}
