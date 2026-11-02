package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;

/**
 * Filtros con los que el dominio pide reservas a su puerto de repositorio (7.5). Todos son opcionales:
 * un campo nulo significa "sin filtrar por eso". Las fechas se aplican a la fecha de entrada de la estancia.
 */
public record CriterioBusquedaReserva(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        EstadoReserva estado,
        IdentificacionApartamento apartamento,
        CanalOrigen canal,
        CodigoTitular titular
) {

    public CriterioBusquedaReserva {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new ReglaDominioException("La fecha inicial no puede ser posterior a la fecha final.");
        }
    }

    public static CriterioBusquedaReserva sinFiltros() {
        return new CriterioBusquedaReserva(null, null, null, null, null, null);
    }
}
