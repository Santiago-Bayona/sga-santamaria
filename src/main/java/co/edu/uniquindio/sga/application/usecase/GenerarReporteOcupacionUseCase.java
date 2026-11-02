package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.service.IndicadoresService;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.IndicadorOcupacion;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoReporte;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reporte de ocupación (7.9). El caso de uso solo carga reservas y apartamentos activos; el cálculo lo hace
 * {@link IndicadoresService}.
 */
@Service
public class GenerarReporteOcupacionUseCase {

    private final ReservaRepository reservaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final IndicadoresService indicadoresService;

    public GenerarReporteOcupacionUseCase(ReservaRepository reservaRepository,
                                          ApartamentoRepository apartamentoRepository,
                                          IndicadoresService indicadoresService) {
        this.reservaRepository = reservaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.indicadoresService = indicadoresService;
    }

    @Transactional(readOnly = true)
    public IndicadorOcupacion ejecutar(PeriodoReporte periodo) {

        List<Reserva> reservas = RecorridoDeReservas.todas(reservaRepository, CriterioBusquedaReserva.sinFiltros());
        int apartamentosActivos = apartamentoRepository.buscarActivos().size();

        return indicadoresService.ocupacion(reservas, apartamentosActivos, periodo);
    }
}
