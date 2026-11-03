package co.edu.uniquindio.sga.infrastructure.notification;

import co.edu.uniquindio.sga.domain.entity.Recordatorio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.repository.RecordatorioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tarea programada: consume recordatorios PROGRAMADOS cuya fecha ya llegó (RN-23) y los envía por correo.
 */
@Component
public class EnvioRecordatoriosScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(EnvioRecordatoriosScheduler.class);

    private final RecordatorioRepository recordatorioRepository;
    private final ReservaRepository reservaRepository;
    private final TitularRepository titularRepository;
    private final CorreoNotificacionPort correo;
    private final Clock clock;

    public EnvioRecordatoriosScheduler(RecordatorioRepository recordatorioRepository,
                                       ReservaRepository reservaRepository,
                                       TitularRepository titularRepository,
                                       CorreoNotificacionPort correo,
                                       Clock clock) {
        this.recordatorioRepository = recordatorioRepository;
        this.reservaRepository = reservaRepository;
        this.titularRepository = titularRepository;
        this.correo = correo;
        this.clock = clock;
    }

    @Scheduled(initialDelayString = "${sga.recordatorios.delay-inicial-ms:15000}",
            fixedDelayString = "${sga.recordatorios.intervalo-ms:300000}")
    @Transactional
    public void enviarPendientes() {
        LocalDateTime ahora = LocalDateTime.now(clock);
        List<Recordatorio> pendientes = recordatorioRepository.buscarPorEjecutar(ahora);
        for (Recordatorio recordatorio : pendientes) {
            try {
                Reserva reserva = reservaRepository.obtenerPorCodigo(recordatorio.getReserva()).orElse(null);
                String destinatario = reserva == null ? null
                        : titularRepository.obtenerPorCodigo(reserva.getTitular()).map(Titular::getCorreo).orElse(null);
                if (destinatario == null) {
                    recordatorio.marcarFallido("No se encontró el correo del titular.");
                } else {
                    correo.enviar(destinatario,
                            "Recordatorio de " + recordatorio.getTipo(),
                            "Reserva " + recordatorio.getReserva().valor()
                                    + " — evento " + recordatorio.getMomentoEvento());
                    recordatorio.marcarEnviado();
                }
            } catch (Exception e) {
                LOG.warn("Fallo enviando recordatorio {}: {}", recordatorio.getCodigo().valor(), e.getMessage());
                recordatorio.marcarFallido(e.getMessage());
            }
            recordatorioRepository.guardar(recordatorio);
        }
    }
}
