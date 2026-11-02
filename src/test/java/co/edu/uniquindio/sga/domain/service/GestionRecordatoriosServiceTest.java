package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Recordatorio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.soporte.*;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.estancia;
import static org.junit.jupiter.api.Assertions.*;

/** RN-25 (un solo recordatorio por reserva y tipo), con RN-23 y RN-24 como contexto. */
class GestionRecordatoriosServiceTest {

    private Apartamento apartamento;
    private ReservaRepositoryEnMemoria reservas;
    private RecordatorioRepositoryEnMemoria recordatorios;
    private GestionRecordatoriosService servicio;
    private Reserva confirmada;

    @BeforeEach
    void preparar() {
        apartamento = DatosDePrueba.apartamento("APT-301", 4);
        reservas = new ReservaRepositoryEnMemoria();
        recordatorios = new RecordatorioRepositoryEnMemoria();
        servicio = new GestionRecordatoriosService(recordatorios, reservas,
                new AlojamientoRepositoryEnMemoria(DatosDePrueba.alojamiento()));
        confirmada = DatosDePrueba.reservaConfirmada("RES-1", apartamento, estancia(LocalDate.of(2026, 9, 10), 3));
        reservas.con(confirmada);
    }

    @Test
    @DisplayName("RN-23: al confirmar se programa un recordatorio de ENTRADA y uno de SALIDA")
    void programarRecordatoriosDeEntradaYSalidaAlConfirmar() {
        servicio.programarRecordatoriosDeConfirmacion(confirmada);

        List<Recordatorio> programados = recordatorios.buscarPorReserva(confirmada.getCodigo());
        assertEquals(2, programados.size());
        assertTrue(programados.stream().anyMatch(r -> r.getTipo() == TipoRecordatorio.ENTRADA));
        assertTrue(programados.stream().anyMatch(r -> r.getTipo() == TipoRecordatorio.SALIDA));
        assertTrue(programados.stream().allMatch(r -> r.getEstado() == EstadoRecordatorio.PROGRAMADO));
    }

    @Test
    @DisplayName("RN-25: no se programa un segundo recordatorio del mismo tipo para la misma reserva")
    void noPermitirSegundoRecordatorioDelMismoTipo() {
        servicio.programarRecordatoriosDeConfirmacion(confirmada);

        assertThrows(ReglaDominioException.class,
                () -> servicio.programarRecordatorio(confirmada.getCodigo(), TipoRecordatorio.ENTRADA));
        assertThrows(ReglaDominioException.class,
                () -> servicio.programarRecordatoriosDeConfirmacion(confirmada));
        assertEquals(2, recordatorios.buscarPorReserva(confirmada.getCodigo()).size());
    }

    @Test
    @DisplayName("RN-25: un recordatorio CANCELADO no ocupa el cupo y se puede programar otro")
    void permitirNuevoRecordatorioSiElAnteriorFueCancelado() {
        Recordatorio entrada = servicio.programarRecordatorio(confirmada.getCodigo(), TipoRecordatorio.ENTRADA);
        entrada.cancelar();
        recordatorios.guardar(entrada);

        Recordatorio nuevo = servicio.programarRecordatorio(confirmada.getCodigo(), TipoRecordatorio.ENTRADA);

        assertEquals(EstadoRecordatorio.PROGRAMADO, nuevo.getEstado());
        assertNotEquals(entrada.getCodigo(), nuevo.getCodigo());
    }

    @Test
    @DisplayName("RN-25: reserva y tipo distintos son independientes (una reserva puede tener ENTRADA y SALIDA)")
    void permitirUnRecordatorioPorCadaTipo() {
        servicio.programarRecordatorio(confirmada.getCodigo(), TipoRecordatorio.ENTRADA);

        Recordatorio salida = servicio.programarRecordatorio(confirmada.getCodigo(), TipoRecordatorio.SALIDA);

        assertEquals(TipoRecordatorio.SALIDA, salida.getTipo());
    }

    @Test
    @DisplayName("RN-23: una reserva PENDIENTE no puede tener recordatorios programados")
    void noProgramarRecordatoriosParaReservaPendiente() {
        Reserva pendiente = DatosDePrueba.reservaPendiente("RES-P", apartamento, estancia(LocalDate.of(2026, 10, 1), 2));
        reservas.con(pendiente);

        assertThrows(ReglaDominioException.class,
                () -> servicio.programarRecordatorio(pendiente.getCodigo(), TipoRecordatorio.ENTRADA));
    }

    @Test
    @DisplayName("RN-24: al cancelar la reserva no quedan recordatorios pendientes")
    void cancelarPendientesDejaTodosEnCancelado() {
        servicio.programarRecordatoriosDeConfirmacion(confirmada);

        servicio.cancelarPendientes(confirmada.getCodigo());

        assertTrue(recordatorios.buscarPorReserva(confirmada.getCodigo()).stream()
                .allMatch(r -> r.getEstado() == EstadoRecordatorio.CANCELADO));
    }
}
