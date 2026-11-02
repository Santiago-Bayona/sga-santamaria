package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.entity.ConflictoCanal;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.CanalExternoRepository;
import co.edu.uniquindio.sga.domain.repository.ConflictoCanalRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import co.edu.uniquindio.sga.domain.valueobject.CodigoConflictoCanal;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.MotivoCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 7.8 canal externo. RN-19: un mensaje repetido no crea dos reservas (ni dos conflictos).
 * RN-18: una reserva externa que colisiona con una vigente se rechaza y se registra como conflicto;
 * nunca sobrescribe la existente. Ambas exigen ver el conjunto de reservas, no una sola.
 * La autenticación de la credencial del canal es de la infraestructura; aquí solo se exige que el canal exista y esté activo.
 */
public class ConciliacionCanalExternoService {

    public enum TipoResultado { ACEPTADA, DUPLICADA, CONFLICTO }

    /** reserva: la creada o la ya existente (ACEPTADA/DUPLICADA). conflicto: el registrado (CONFLICTO). */
    public record ResultadoConciliacion(TipoResultado tipo, Reserva reserva, ConflictoCanal conflicto) {
    }

    private final ReservaRepository reservaRepository;
    private final ConflictoCanalRepository conflictoRepository;
    private final CanalExternoRepository canalRepository;
    private final CreacionReservaService creacion;
    private final CancelacionReservaService cancelacion;

    public ConciliacionCanalExternoService(ReservaRepository reservaRepository,
                                           ConflictoCanalRepository conflictoRepository,
                                           CanalExternoRepository canalRepository,
                                           CreacionReservaService creacion, CancelacionReservaService cancelacion) {
        this.reservaRepository = reservaRepository;
        this.conflictoRepository = conflictoRepository;
        this.canalRepository = canalRepository;
        this.creacion = creacion;
        this.cancelacion = cancelacion;
    }

    public ResultadoConciliacion conciliar(CodigoCanalExterno codigoCanal, IdentificadorExterno identificador,
                                           Titular titular, IdentificacionApartamento apartamento, Estancia estancia,
                                           List<Ocupante> acompanantes, LocalTime horaLlegadaEstimada,
                                           LocalDateTime ahora) {
        exigirCanalActivo(codigoCanal);
        if (identificador == null) {
            throw new ReglaDominioException("Toda reserva externa llega con el identificador propio del canal.");
        }

        // RN-19: el mismo mensaje recibido otra vez devuelve lo que ya se hizo con él
        Optional<Reserva> existente = reservaRepository.obtenerPorIdentificadorExterno(CanalOrigen.EXTERNO, identificador);
        if (existente.isPresent()) {
            return new ResultadoConciliacion(TipoResultado.DUPLICADA, existente.get(), null);
        }
        Optional<ConflictoCanal> conflictoPrevio = conflictoRepository.obtenerPorIdentificadorExterno(identificador);
        if (conflictoPrevio.isPresent()) {
            return new ResultadoConciliacion(TipoResultado.CONFLICTO, null, conflictoPrevio.get());
        }

        // RN-18: colisión con una reserva vigente → se rechaza y se registra, la vigente no se toca
        Optional<Reserva> colision = reservaRepository.buscarActivasPorApartamento(apartamento).stream()
                .filter(reserva -> reserva.seSolapaCon(estancia))
                .findFirst();
        if (colision.isPresent()) {
            ConflictoCanal conflicto = new ConflictoCanal(new CodigoConflictoCanal("CON-" + identificador.valor()),
                    CanalOrigen.EXTERNO, identificador, apartamento, estancia, colision.get().getCodigo(), ahora);
            conflictoRepository.guardar(conflicto);
            return new ResultadoConciliacion(TipoResultado.CONFLICTO, null, conflicto);
        }

        // Sin colisión: misma creación y mismas validaciones que portal y directo (F-10)
        Reserva creada = creacion.crear(titular, apartamento, estancia, acompanantes, horaLlegadaEstimada,
                CanalOrigen.EXTERNO, identificador, ahora);
        return new ResultadoConciliacion(TipoResultado.ACEPTADA, creada, null);
    }

    /** Cancelación enviada por el canal. Idempotente: si ya estaba cancelada, no hace nada. */
    public Optional<LiquidacionPolitica> cancelarReservaExterna(CodigoCanalExterno codigoCanal,
                                                                IdentificadorExterno identificador,
                                                                LocalDateTime ahora) {
        exigirCanalActivo(codigoCanal);
        Reserva reserva = reservaRepository.obtenerPorIdentificadorExterno(CanalOrigen.EXTERNO, identificador)
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe una reserva externa con identificador " + identificador.valor()));
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            return Optional.empty();
        }
        return Optional.of(cancelacion.cancelar(reserva.getCodigo(),
                new MotivoCancelacion("Cancelación recibida del canal " + codigoCanal.valor()), ahora));
    }

    private void exigirCanalActivo(CodigoCanalExterno codigoCanal) {
        CanalExterno canal = canalRepository.obtenerPorCodigo(codigoCanal)
                .orElseThrow(() -> new ReglaDominioException("No existe el canal " + codigoCanal.valor()));
        if (!canal.puedeVender()) {
            throw new ReglaDominioException("El canal " + codigoCanal.valor() + " está desactivado.");
        }
    }
}
