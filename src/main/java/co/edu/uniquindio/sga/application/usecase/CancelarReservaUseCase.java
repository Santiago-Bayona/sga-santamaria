package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.CancelacionReservaService;
import co.edu.uniquindio.sga.domain.service.LiquidacionPolitica;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.MotivoCancelacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelarReservaUseCase {

    private final CancelacionReservaService cancelacionReservaService;

    public CancelarReservaUseCase(CancelacionReservaService cancelacionReservaService) {
        this.cancelacionReservaService = cancelacionReservaService;
    }

    @Transactional
    public LiquidacionPolitica ejecutar(CodigoReserva codigoReserva, MotivoCancelacion motivo, LocalDateTime momentoActual) {

        return cancelacionReservaService.cancelar(codigoReserva, motivo, momentoActual);
    }
}
