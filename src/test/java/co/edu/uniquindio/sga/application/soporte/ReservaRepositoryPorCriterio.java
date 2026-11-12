package co.edu.uniquindio.sga.application.soporte;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.soporte.ReservaRepositoryEnMemoria;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;

import java.util.Comparator;
import java.util.List;

/**
 * Repositorio de reservas en memoria que sí respeta el criterio de búsqueda por estado y por fecha de entrada y
 * pagina de a {@link Pagina#TAMANO}, como el adaptador real. Lo necesitan los casos de uso de llegadas y salidas.
 */
public class ReservaRepositoryPorCriterio extends ReservaRepositoryEnMemoria {

    @Override
    public Pagina<Reserva> buscar(CriterioBusquedaReserva criterio, int pagina) {
        List<Reserva> coinciden = buscarPorEstado(criterio.estado()).stream()
                .filter(r -> criterio.fechaDesde() == null || !r.getEstancia().entrada().isBefore(criterio.fechaDesde()))
                .filter(r -> criterio.fechaHasta() == null || !r.getEstancia().entrada().isAfter(criterio.fechaHasta()))
                .sorted(Comparator.comparing(r -> r.getCodigo().valor()))
                .toList();
        int desde = Math.min(pagina * Pagina.TAMANO, coinciden.size());
        int hasta = Math.min(desde + Pagina.TAMANO, coinciden.size());
        return new Pagina<>(coinciden.subList(desde, hasta), pagina, coinciden.size());
    }
}
