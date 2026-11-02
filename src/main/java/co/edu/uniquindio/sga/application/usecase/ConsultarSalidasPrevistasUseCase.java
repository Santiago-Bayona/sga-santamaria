package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/** Salidas previstas de un día (7.6): reservas EN_CURSO cuya fecha de salida es esa fecha. */

@Service
public class ConsultarSalidasPrevistasUseCase {

    private final ReservaRepository reservaRepository;

    public ConsultarSalidasPrevistasUseCase(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    /**
     * Salidas del día: reservas EN_CURSO cuya salida es la fecha dada. El criterio de búsqueda filtra por fecha de
     * entrada, así que aquí se filtra por salida sobre las EN_CURSO (a lo sumo una por apartamento) y se pagina.
     */
    @Transactional(readOnly = true)
    public Pagina<Reserva> ejecutar(LocalDate fecha, int pagina) {

        List<Reserva> delDia = reservaRepository.buscarPorEstado(EstadoReserva.EN_CURSO).stream()
                .filter(reserva -> reserva.getEstancia().salida().equals(fecha))
                .sorted(Comparator.comparing(reserva -> reserva.getApartamento().valor()))
                .toList();

        return PaginacionEnMemoria.de(delDia, pagina);
    }
}
