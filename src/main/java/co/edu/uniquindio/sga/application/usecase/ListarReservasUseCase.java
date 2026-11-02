package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listado de reservas para recepción y administración (7.5): más reciente primero, paginado, con filtros opcionales por
 * fechas, estado, apartamento, canal y titular. El filtrado y la paginación los hace el repositorio.
 */

@Service
public class ListarReservasUseCase {

    private final ReservaRepository reservaRepository;

    public ListarReservasUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<Reserva> ejecutar(CriterioBusquedaReserva criterio, int pagina) {

        if (pagina < 0) {
            throw new SolicitudAplicacionException("El número de página no puede ser negativo.");
        }

        return reservaRepository.buscar(criterio, pagina);
    }

}
