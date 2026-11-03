package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.service.IndicadoresService;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoReporte;
import co.edu.uniquindio.sga.domain.valueobject.ReservasDeCanal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reservas por canal de origen (7.9). El caso de uso solo carga las reservas; el cálculo lo hace
 * {@link IndicadoresService}.
 */
@Service
public class GenerarReporteReservasPorCanalUseCase {

    private final ReservaRepository reservaRepository;
    private final IndicadoresService indicadoresService;

    public GenerarReporteReservasPorCanalUseCase(ReservaRepository reservaRepository,
                                                 IndicadoresService indicadoresService) {
        this.reservaRepository = reservaRepository;
        this.indicadoresService = indicadoresService;
    }

    @Transactional(readOnly = true)
    public List<ReservasDeCanal> ejecutar(PeriodoReporte periodo) {

        List<Reserva> reservas = RecorridoDeReservas.todas(reservaRepository, CriterioBusquedaReserva.sinFiltros());

        return indicadoresService.reservasPorCanal(reservas, periodo);
    }
}
