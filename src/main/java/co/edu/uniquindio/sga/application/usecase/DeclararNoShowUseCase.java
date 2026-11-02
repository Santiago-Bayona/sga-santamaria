package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.DeclaracionNoShowService;
import co.edu.uniquindio.sga.domain.service.LiquidacionPolitica;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DeclararNoShowUseCase {

    private final DeclaracionNoShowService declaracionNoShowService;

    public DeclararNoShowUseCase(
            DeclaracionNoShowService declaracionNoShowService) {
        this.declaracionNoShowService = declaracionNoShowService;
    }

    @Transactional
    public LiquidacionPolitica ejecutar(CodigoReserva codigoReserva, LocalDateTime momentoActual) {

        return declaracionNoShowService.declararNoShow(codigoReserva, momentoActual);
    }
}