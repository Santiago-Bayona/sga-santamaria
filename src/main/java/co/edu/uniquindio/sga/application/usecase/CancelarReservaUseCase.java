package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
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
    private final ReservaRepository reservaRepository;
    private final AvisadorReserva avisador;

    public CancelarReservaUseCase(CancelacionReservaService cancelacionReservaService,
                                  ReservaRepository reservaRepository, AvisadorReserva avisador) {
        this.cancelacionReservaService = cancelacionReservaService;
        this.reservaRepository = reservaRepository;
        this.avisador = avisador;
    }

    @Transactional
    public LiquidacionPolitica ejecutar(CodigoReserva codigoReserva, MotivoCancelacion motivo, LocalDateTime momentoActual) {

        LiquidacionPolitica liquidacion = cancelacionReservaService.cancelar(codigoReserva, motivo, momentoActual);
        reservaRepository.obtenerPorCodigo(codigoReserva)
                .ifPresent(reserva -> avisador.cancelada(reserva, liquidacion.retencion()));
        return liquidacion;
    }
}
