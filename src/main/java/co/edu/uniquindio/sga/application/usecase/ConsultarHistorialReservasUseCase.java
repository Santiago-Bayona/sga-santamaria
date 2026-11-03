package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.TitularNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Historial de reservas de un titular, de la más reciente a la más antigua (7.5).
 *
 */

@Service
public class ConsultarHistorialReservasUseCase {

    private final ReservaRepository reservaRepository;
    private final TitularRepository titularRepository;

    public ConsultarHistorialReservasUseCase(ReservaRepository reservaRepository, TitularRepository titularRepository) {
        this.reservaRepository = reservaRepository;
        this.titularRepository = titularRepository;
    }

    /** Historial del huésped: el repositorio filtra por titular, ordena (más reciente primero) y pagina. */
    @Transactional(readOnly = true)
    public Pagina<Reserva> ejecutar(CodigoTitular codigo, int pagina) {

        titularRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new TitularNoEncontradoException(codigo));

        return reservaRepository.buscar(
                new CriterioBusquedaReserva(null, null, null, null, null, codigo), pagina);
    }
}
