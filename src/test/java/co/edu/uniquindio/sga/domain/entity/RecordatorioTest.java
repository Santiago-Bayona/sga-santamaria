package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Ciclo de vida del recordatorio (RN-23/RN-24/RN-25): estados, antelación y cupo por reserva y tipo. */
class RecordatorioTest {

    private static final LocalDateTime EVENTO = LocalDateTime.of(2026, 9, 10, 15, 0);
    private static final CodigoReserva RESERVA = new CodigoReserva("RES-1");
    private Recordatorio recordatorio;

    @BeforeEach
    void preparar() {
        recordatorio = Recordatorio.programar(new CodigoRecordatorio("REC-1"), RESERVA, TipoRecordatorio.ENTRADA,
                EVENTO, Duration.ofHours(24));
    }

    @Test
    @DisplayName("Se programa antes del evento, según la antelación, y nace PROGRAMADO")
    void programarConAntelacion() {
        assertEquals(EVENTO.minusHours(24), recordatorio.getFechaRecordatorio());
        assertEquals(EstadoRecordatorio.PROGRAMADO, recordatorio.getEstado());
        assertFalse(recordatorio.debeEjecutarse(EVENTO.minusHours(25)));
        assertTrue(recordatorio.debeEjecutarse(EVENTO.minusHours(24)));
    }

    @Test
    @DisplayName("La antelación debe ser mayor que cero")
    void noProgramarSinAntelacionPositiva() {
        assertThrows(ReglaDominioException.class, () -> Recordatorio.programar(new CodigoRecordatorio("R"), RESERVA,
                TipoRecordatorio.SALIDA, EVENTO, Duration.ZERO));
        assertThrows(ReglaDominioException.class, () -> Recordatorio.programar(new CodigoRecordatorio("R"), RESERVA,
                TipoRecordatorio.SALIDA, EVENTO, Duration.ofHours(-1)));
    }

    @Test
    @DisplayName("RN-24: un recordatorio enviado o cancelado es terminal; uno fallido puede reintentarse o cancelarse")
    void respetarLasTransicionesDeEstado() {
        recordatorio.marcarFallido("SMTP caído");
        assertTrue(recordatorio.estaPendiente());
        recordatorio.marcarEnviado();
        assertFalse(recordatorio.estaPendiente());
        assertThrows(ReglaDominioException.class, recordatorio::cancelar);
        assertThrows(ReglaDominioException.class, () -> recordatorio.marcarFallido("otro"));
    }

    @Test
    @DisplayName("Un fallo exige registrar el error")
    void exigirErrorAlMarcarFallido() {
        assertThrows(ReglaDominioException.class, () -> recordatorio.marcarFallido(" "));
        assertEquals(EstadoRecordatorio.PROGRAMADO, recordatorio.getEstado());
    }

    @Test
    @DisplayName("RN-25: ocupa cupo para su reserva y tipo mientras no esté cancelado")
    void ocuparCupoMientrasNoEsteCancelado() {
        assertTrue(recordatorio.ocupaCupo(RESERVA, TipoRecordatorio.ENTRADA));
        assertFalse(recordatorio.ocupaCupo(RESERVA, TipoRecordatorio.SALIDA));
        assertFalse(recordatorio.ocupaCupo(new CodigoReserva("OTRA"), TipoRecordatorio.ENTRADA));
        recordatorio.cancelar();
        assertFalse(recordatorio.ocupaCupo(RESERVA, TipoRecordatorio.ENTRADA));
    }

    @Test
    @DisplayName("Solo se reprograma un recordatorio PROGRAMADO")
    void noReprogramarUnRecordatorioEnviado() {
        recordatorio.reprogramar(EVENTO.plusDays(1), Duration.ofHours(24));
        assertEquals(EVENTO.plusDays(1).minusHours(24), recordatorio.getFechaRecordatorio());
        recordatorio.marcarEnviado();

        assertThrows(ReglaDominioException.class, () -> recordatorio.reprogramar(EVENTO, Duration.ofHours(24)));
    }
}
