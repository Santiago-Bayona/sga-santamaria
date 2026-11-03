package co.edu.uniquindio.sga.infrastructure.notification;

import co.edu.uniquindio.sga.application.usecase.VencerReservasPendientesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Materializa el caso de uso RN-21: cancela automáticamente reservas PENDIENTE que superaron el plazo.
 */
@Component
public class VencimientoReservasScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(VencimientoReservasScheduler.class);

    private final VencerReservasPendientesUseCase useCase;
    private final Clock reloj;

    public VencimientoReservasScheduler(VencerReservasPendientesUseCase useCase, Clock reloj) {
        this.useCase = useCase;
        this.reloj = reloj;
    }

    @Scheduled(initialDelayString = "${sga.vencimientos.delay-inicial-ms:20000}",
            fixedDelayString = "${sga.vencimientos.intervalo-ms:3600000}")
    public void vencerPendientes() {
        var vencidas = useCase.ejecutar(LocalDateTime.now(reloj));
        if (!vencidas.isEmpty()) {
            LOG.info("Reservas vencidas automáticamente: {}", vencidas.size());
        }
    }
}
