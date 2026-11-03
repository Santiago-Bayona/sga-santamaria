package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.dto.response.CalendarioOcupacionResponse;
import co.edu.uniquindio.sga.application.dto.response.NocheCalendarioResponse;
import co.edu.uniquindio.sga.application.dto.response.SituacionNoche;
import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Noche;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Calendario de ocupación de un apartamento (7.5): para cada noche del rango, si está OCUPADA (reserva activa),
 * BLOQUEADA (bloqueo vigente) o DISPONIBLE.
 */

@Service
public class ConsultarCalendarioOcupacionUseCase {

    private final ApartamentoRepository apartamentoRepository;
    private final ReservaRepository reservaRepository;
    private final BloqueoRepository bloqueoRepository;

    public ConsultarCalendarioOcupacionUseCase(ApartamentoRepository apartamentoRepository,
                                               ReservaRepository reservaRepository,
                                               BloqueoRepository bloqueoRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.reservaRepository = reservaRepository;
        this.bloqueoRepository = bloqueoRepository;
    }

    @Transactional(readOnly = true)
    public CalendarioOcupacionResponse ejecutar(IdentificacionApartamento identificacion, Estancia rango) {

        apartamentoRepository.obtenerPorIdentificacion(identificacion)
                .orElseThrow(() -> new ApartamentoNoEncontradoException(identificacion));

        // El rango es una Estancia: el dominio exige al menos una noche (RN-03).
        List<Reserva> reservas = reservaRepository.buscarActivasPorApartamento(identificacion);
        List<Bloqueo> bloqueos = bloqueoRepository.buscarVigentesPorApartamento(identificacion);

        List<NocheCalendarioResponse> noches = rango.noches().stream()
                .map(noche -> convertirNoche(noche, reservas, bloqueos))
                .toList();

        return new CalendarioOcupacionResponse(
                identificacion.valor(),
                rango.entrada(),
                rango.salida(),
                noches
        );
    }

    private NocheCalendarioResponse convertirNoche(Noche noche, List<Reserva> reservas, List<Bloqueo> bloqueos) {

        // Una reserva activa y un bloqueo no pueden coincidir en la misma noche (lo impide el dominio).
        return reservas.stream()
                .filter(reserva -> reserva.ocupa(noche))
                .findFirst()
                .map(reserva -> new NocheCalendarioResponse(
                        noche.fecha(), SituacionNoche.OCUPADA,
                        reserva.getCodigo().valor(), reserva.getEstado(), null))
                .orElseGet(() -> bloqueos.stream()
                        .filter(bloqueo -> bloqueo.bloqueaNoche(noche))
                        .findFirst()
                        .map(bloqueo -> new NocheCalendarioResponse(
                                noche.fecha(), SituacionNoche.BLOQUEADA,
                                null, null, bloqueo.getMotivo().descripcion()))
                        .orElseGet(() -> new NocheCalendarioResponse(
                                noche.fecha(), SituacionNoche.DISPONIBLE, null, null, null)));
    }
}
