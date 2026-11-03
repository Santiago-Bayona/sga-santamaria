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
import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;

import java.time.LocalDateTime;

/**
 * 7.5 no-show: se declara desde la hora límite configurada del día de entrada, aplica la consecuencia de la
 * versión de política congelada (RN-13) y libera la disponibilidad (RN-12).
 */
public class DeclaracionNoShowService {

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final PoliticaCancelacionRepository politicaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final GestionRecordatoriosService recordatorios;

    public DeclaracionNoShowService(ReservaRepository reservaRepository, FolioRepository folioRepository,
                                    PoliticaCancelacionRepository politicaRepository,
                                    AlojamientoRepository alojamientoRepository,
                                    GestionRecordatoriosService recordatorios) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.politicaRepository = politicaRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.recordatorios = recordatorios;
    }

    public LiquidacionPolitica declararNoShow(CodigoReserva codigo, LocalDateTime ahora) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReglaDominioException("No existe la reserva " + codigo.valor()));
        Folio folio = folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new ReglaDominioException("La reserva " + codigo.valor() + " no tiene folio."));
        PoliticaCancelacion politica = politicaRepository.obtenerPorVersion(reserva.getPoliticaVersionCongelada())
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe la versión " + reserva.getPoliticaVersionCongelada().numero() + " de la política."));
        ConfiguracionAlojamiento configuracion = alojamientoRepository.obtener().getConfiguracion();

        // Valida la hora límite y que la reserva esté CONFIRMADA (RN-08)
        reserva.declararNoShow(ahora, configuracion.horaLimiteNoShow());

        Dinero anticipo = configuracion.anticipoSobre(reserva.getValorCongelado());
        Dinero retencion = politica.calcularRetencionPorNoShow(ValidacionesReserva.baseRetencion(anticipo, folio));
        folio.liquidarRetencion(retencion, ahora.toLocalDate());

        reservaRepository.guardar(reserva);
        folioRepository.guardar(folio);
        recordatorios.cancelarPendientes(codigo); // RN-24
        return new LiquidacionPolitica(codigo, retencion, folio.calcularSaldo());
    }
}
