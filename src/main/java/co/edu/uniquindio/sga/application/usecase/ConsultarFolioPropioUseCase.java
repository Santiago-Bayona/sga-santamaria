package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.FolioNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.ReservaNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Folio de una reserva, SOLO si pertenece al titular indicado (7.2: la restricción se verifica sobre el propietario
 * del recurso, no solo sobre el rol). Si la reserva es de otro titular responde "no encontrada" (no revela que existe).
 */

@Service
public class ConsultarFolioPropioUseCase {

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;

    public ConsultarFolioPropioUseCase(ReservaRepository reservaRepository, FolioRepository folioRepository) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
    }

    /**
     * Folio de una reserva visto por su huésped: solo si la reserva es suya. Si no lo es, responde igual que si
     * no existiera, para no revelar reservas ajenas.
     */
    @Transactional(readOnly = true)
    public Folio ejecutar(CodigoReserva codigoReserva, CodigoTitular titular) {

        Reserva reserva = reservaRepository.obtenerPorCodigo(codigoReserva)
                .orElseThrow(() -> new ReservaNoEncontradaException(codigoReserva));

        if (!reserva.getTitular().equals(titular)) {
            throw new ReservaNoEncontradaException(codigoReserva);
        }

        return folioRepository.obtenerPorReserva(codigoReserva)
                .orElseThrow(() -> new FolioNoEncontradoException(codigoReserva));
    }
}
