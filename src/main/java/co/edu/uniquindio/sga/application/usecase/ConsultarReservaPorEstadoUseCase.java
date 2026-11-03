package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarReservaPorEstadoUseCase {

    private final ReservaRepository reservaRepository;

    public ConsultarReservaPorEstadoUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<Reserva> ejecutar(EstadoReserva estado, int pagina) {

        return reservaRepository.buscar(
                new CriterioBusquedaReserva(null, null, estado, null, null, null), pagina);
    }
}