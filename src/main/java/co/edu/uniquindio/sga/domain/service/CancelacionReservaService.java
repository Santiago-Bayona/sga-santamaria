package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.MotivoCancelacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 7.5 cancelación: solo desde PENDIENTE o CONFIRMADA (lo valida Reserva), con la retención de la versión de
 * política congelada en la reserva (RN-13), registrada en el folio como penalidad y saldo a favor.
 * La reserva deja de estar activa y libera sus noches de inmediato (RN-12).
 */
public class CancelacionReservaService {

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final PoliticaCancelacionRepository politicaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final GestionRecordatoriosService recordatorios;

    public CancelacionReservaService(ReservaRepository reservaRepository, FolioRepository folioRepository,
                                     PoliticaCancelacionRepository politicaRepository,
                                     AlojamientoRepository alojamientoRepository,
                                     GestionRecordatoriosService recordatorios) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.politicaRepository = politicaRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.recordatorios = recordatorios;
    }

    public LiquidacionPolitica cancelar(CodigoReserva codigo, MotivoCancelacion motivo, LocalDateTime ahora) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReglaDominioException("No existe la reserva " + codigo.valor()));
        Folio folio = folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new ReglaDominioException("La reserva " + codigo.valor() + " no tiene folio."));
        // RN-13: la versión congelada, no la vigente al momento de cancelar
        PoliticaCancelacion politica = politicaRepository.obtenerPorVersion(reserva.getPoliticaVersionCongelada())
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe la versión " + reserva.getPoliticaVersionCongelada().numero() + " de la política."));

        reserva.cancelar(motivo); // RN-08: rechaza si no está PENDIENTE o CONFIRMADA

        LocalDate hoy = ahora.toLocalDate();
        Dinero anticipo = alojamientoRepository.obtener().getConfiguracion().anticipoSobre(reserva.getValorCongelado());
        Dinero retencion = politica.calcularRetencionPorCancelacion(
                ValidacionesReserva.baseRetencion(anticipo, folio), hoy, reserva.getEstancia());
        folio.liquidarRetencion(retencion, hoy);

        reservaRepository.guardar(reserva);
        folioRepository.guardar(folio);
        recordatorios.cancelarPendientes(codigo); // RN-24
        return new LiquidacionPolitica(codigo, retencion, folio.calcularSaldo());
    }
}
