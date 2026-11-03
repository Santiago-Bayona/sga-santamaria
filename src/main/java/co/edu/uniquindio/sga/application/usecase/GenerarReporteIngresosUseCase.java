package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.service.IndicadoresService;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.IndicadorIngresos;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoReporte;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Reporte de ingresos (7.9). El caso de uso solo carga los folios; el cálculo lo hace {@link IndicadoresService}.
 */
@Service
public class GenerarReporteIngresosUseCase {

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final IndicadoresService indicadoresService;

    public GenerarReporteIngresosUseCase(ReservaRepository reservaRepository, FolioRepository folioRepository,
                                         IndicadoresService indicadoresService) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.indicadoresService = indicadoresService;
    }

    @Transactional(readOnly = true)
    public IndicadorIngresos ejecutar(PeriodoReporte periodo) {

        List<Folio> folios = RecorridoDeReservas.todas(reservaRepository, CriterioBusquedaReserva.sinFiltros()).stream()
                .map(reserva -> folioRepository.obtenerPorReserva(reserva.getCodigo()))
                .flatMap(Optional::stream)
                .toList();

        return indicadoresService.ingresos(folios, periodo);
    }
}
