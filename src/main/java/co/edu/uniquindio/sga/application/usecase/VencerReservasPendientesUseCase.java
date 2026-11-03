package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.service.VencimientoReservasPendientesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VencerReservasPendientesUseCase {

    private final VencimientoReservasPendientesService vencimientoService;

    public VencerReservasPendientesUseCase(VencimientoReservasPendientesService vencimientoService) {
        this.vencimientoService = vencimientoService;
    }

    @Transactional
    public List<Reserva> ejecutar(LocalDateTime momentoActual) {
        return vencimientoService.cancelarVencidas(momentoActual);
    }
}