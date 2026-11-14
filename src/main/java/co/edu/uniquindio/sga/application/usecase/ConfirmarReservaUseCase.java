package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.service.ConfirmacionReservaService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ConfirmarReservaUseCase {

    private final ConfirmacionReservaService confirmacionReservaService;
    private final AvisadorReserva avisador;

    public ConfirmarReservaUseCase(ConfirmacionReservaService confirmacionReservaService, AvisadorReserva avisador) {
        this.confirmacionReservaService = confirmacionReservaService;
        this.avisador = avisador;
    }

    @Transactional
    public Reserva ejecutar(CodigoReserva codigoReserva, LocalDateTime momentoActual) {

        Reserva reserva = confirmacionReservaService.confirmar(codigoReserva, momentoActual);
        avisador.confirmada(reserva);
        return reserva;
    }
}
