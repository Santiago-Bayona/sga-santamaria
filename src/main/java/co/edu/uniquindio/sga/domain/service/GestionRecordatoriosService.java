package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Recordatorio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.RecordatorioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoRecordatorio;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.EstadoRecordatorio;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.TipoRecordatorio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * RN-23 (programar al confirmar), RN-24 (una reserva que deja de estar activa no conserva recordatorios pendientes)
 * y RN-25 (un solo recordatorio por reserva y tipo). La unicidad exige ver todos los recordatorios de la reserva.
 */
public class GestionRecordatoriosService {

    private final RecordatorioRepository recordatorioRepository;
    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;

    public GestionRecordatoriosService(RecordatorioRepository recordatorioRepository,
                                       ReservaRepository reservaRepository,
                                       AlojamientoRepository alojamientoRepository) {
        this.recordatorioRepository = recordatorioRepository;
        this.reservaRepository = reservaRepository;
        this.alojamientoRepository = alojamientoRepository;
    }

    /** RN-23: toda reserva confirmada genera un recordatorio de entrada y otro de salida. */
    public void programarRecordatoriosDeConfirmacion(Reserva reserva) {
        programar(reserva, TipoRecordatorio.ENTRADA);
        programar(reserva, TipoRecordatorio.SALIDA);
    }

    public Recordatorio programarRecordatorio(CodigoReserva codigoReserva, TipoRecordatorio tipo) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigoReserva)
                .orElseThrow(() -> new ReglaDominioException("No existe la reserva " + codigoReserva.valor()));
        return programar(reserva, tipo);
    }

    /** Si cambian las fechas de la reserva (RN-14), los recordatorios se mueven con ella. */
    public void reprogramarPorModificacion(Reserva reserva) {
        ConfiguracionAlojamiento configuracion = alojamientoRepository.obtener().getConfiguracion();
        for (Recordatorio recordatorio : recordatorioRepository.buscarPorReserva(reserva.getCodigo())) {
            TipoRecordatorio tipo = recordatorio.getTipo();
            if (recordatorio.getEstado() == EstadoRecordatorio.PROGRAMADO) {
                recordatorio.reprogramar(momentoEvento(reserva, tipo, configuracion), antelacion(tipo, configuracion));
                recordatorioRepository.guardar(recordatorio);
            } else if (recordatorio.getEstado() == EstadoRecordatorio.FALLIDO) {
                // Un FALLIDO no se reprograma: se cancela y se crea uno nuevo para la nueva fecha
                recordatorio.cancelar();
                recordatorioRepository.guardar(recordatorio);
                programar(reserva, tipo);
            }
        }
    }

    /** RN-24: al cancelar, vencer, declarar no-show o registrar la salida no quedan recordatorios pendientes. */
    public void cancelarPendientes(CodigoReserva codigoReserva) {
        for (Recordatorio recordatorio : recordatorioRepository.buscarPorReserva(codigoReserva)) {
            if (recordatorio.estaPendiente()) {
                recordatorio.cancelar();
                recordatorioRepository.guardar(recordatorio);
            }
        }
    }

    private Recordatorio programar(Reserva reserva, TipoRecordatorio tipo) {
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA && reserva.getEstado() != EstadoReserva.EN_CURSO) {
            throw new ReglaDominioException("Solo una reserva CONFIRMADA o EN_CURSO puede tener recordatorios programados.");
        }
        List<Recordatorio> existentes = recordatorioRepository.buscarPorReserva(reserva.getCodigo());
        // RN-25: un solo recordatorio (no cancelado) por reserva y tipo de evento
        if (existentes.stream().anyMatch(r -> r.ocupaCupo(reserva.getCodigo(), tipo))) {
            throw new ReglaDominioException("La reserva ya tiene un recordatorio de " + tipo + " programado.");
        }
        ConfiguracionAlojamiento configuracion = alojamientoRepository.obtener().getConfiguracion();
        CodigoRecordatorio codigo = new CodigoRecordatorio(
                "REC-" + reserva.getCodigo().valor() + "-" + tipo + "-" + (existentes.size() + 1));
        Recordatorio recordatorio = Recordatorio.programar(codigo, reserva.getCodigo(), tipo,
                momentoEvento(reserva, tipo, configuracion), antelacion(tipo, configuracion));
        recordatorioRepository.guardar(recordatorio);
        return recordatorio;
    }

    private static LocalDateTime momentoEvento(Reserva reserva, TipoRecordatorio tipo, ConfiguracionAlojamiento c) {
        return tipo == TipoRecordatorio.ENTRADA
                ? reserva.getEstancia().entrada().atTime(c.horaEntrada())
                : reserva.getEstancia().salida().atTime(c.horaSalida());
    }

    private static Duration antelacion(TipoRecordatorio tipo, ConfiguracionAlojamiento c) {
        return tipo == TipoRecordatorio.ENTRADA ? c.antelacionRecordatorioEntrada() : c.antelacionRecordatorioSalida();
    }
}
