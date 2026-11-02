package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.valueobject.Dinero;

import java.util.List;

/**
 * Filtros opcionales de la búsqueda de disponibilidad (7.5). Un campo nulo o vacío significa "sin filtrar por eso".
 *
 * @param capacidadMinima capacidad mínima del apartamento
 * @param precioMinimo    valor estimado mínimo de la estancia completa
 * @param precioMaximo    valor estimado máximo de la estancia completa
 * @param caracteristicas elementos de la dotación que el apartamento debe tener (todos)
 * @param servicios       servicios adicionales que el alojamiento debe ofrecer (todos)
 */
public record FiltrosDisponibilidad(
        Integer capacidadMinima,
        Dinero precioMinimo,
        Dinero precioMaximo,
        List<String> caracteristicas,
        List<String> servicios
) {

    public FiltrosDisponibilidad {
        caracteristicas = caracteristicas == null ? List.of() : List.copyOf(caracteristicas);
        servicios = servicios == null ? List.of() : List.copyOf(servicios);
    }

    public static FiltrosDisponibilidad sinFiltros() {
        return new FiltrosDisponibilidad(null, null, null, List.of(), List.of());
    }
}
