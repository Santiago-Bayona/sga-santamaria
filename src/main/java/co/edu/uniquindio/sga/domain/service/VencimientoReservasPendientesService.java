package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.PlazoConfirmacion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RN-21: una reserva PENDIENTE que supera el plazo de confirmación pasa a CANCELADA y libera sus noches,
 * sin intervención manual. La invoca una tarea programada de la infraestructura.
 * El vencimiento no es una cancelación del huésped: no aplica retención.
 */
public class VencimientoReservasPendientesService {

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final GestionRecordatoriosService recordatorios;

    public VencimientoReservasPendientesService(ReservaRepository reservaRepository, FolioRepository folioRepository,
                                                AlojamientoRepository alojamientoRepository,
                                                GestionRecordatoriosService recordatorios) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.recordatorios = recordatorios;
    }

    /** @return las reservas que se cancelaron en esta ejecución */
    public List<Reserva> cancelarVencidas(LocalDateTime ahora) {
        PlazoConfirmacion plazo = alojamientoRepository.obtener().getConfiguracion().plazoConfirmacion();
        List<Reserva> canceladas = new ArrayList<>();
        for (Reserva reserva : reservaRepository.buscarPorEstado(EstadoReserva.PENDIENTE)) {
            if (!reserva.haSuperadoPlazoConfirmacion(ahora, plazo)) {
                continue;
            }
            reserva.cancelarPorVencimiento(ahora, plazo);
            reservaRepository.guardar(reserva);
            // Se anula el cargo de alojamiento; si hubo algún pago queda como saldo a favor
            folioRepository.obtenerPorReserva(reserva.getCodigo()).ifPresent(folio -> {
                folio.liquidarRetencion(Dinero.CERO, ahora.toLocalDate());
                folioRepository.guardar(folio);
            });
            recordatorios.cancelarPendientes(reserva.getCodigo());
            canceladas.add(reserva);
        }
        return canceladas;
    }
}
