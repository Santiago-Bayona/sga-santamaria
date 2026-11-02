package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/** Llegadas previstas de un día (7.6): reservas CONFIRMADAS cuya entrada es esa fecha, por hora estimada de llegada. */

@Service
public class ConsultarLlegadasPrevistasUseCase {

    private final ReservaRepository reservaRepository;

    public ConsultarLlegadasPrevistasUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    /** Llegadas del día (7.5): reservas CONFIRMADAS cuya entrada es la fecha dada. Filtra y pagina el repositorio. */
    @Transactional(readOnly = true)
    public Pagina<Reserva> ejecutar(LocalDate fecha, int pagina) {

        return reservaRepository.buscar(
                new CriterioBusquedaReserva(fecha, fecha, EstadoReserva.CONFIRMADA, null, null, null), pagina);
    }
}
