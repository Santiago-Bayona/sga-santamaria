package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.soporte.*;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.AHORA;
import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.estancia;
import static org.junit.jupiter.api.Assertions.*;

/** RN-21: una reserva PENDIENTE que supera el plazo de confirmación (24 h en la configuración de prueba) se cancela sola. */
class VencimientoReservasPendientesServiceTest {

    private Apartamento apartamento;
    private ReservaRepositoryEnMemoria reservas;
    private FolioRepositoryEnMemoria folios;
    private VencimientoReservasPendientesService servicio;

    @BeforeEach
    void preparar() {
        apartamento = DatosDePrueba.apartamento("APT-301", 4);
        reservas = new ReservaRepositoryEnMemoria();
        folios = new FolioRepositoryEnMemoria();
        AlojamientoRepositoryEnMemoria alojamiento = new AlojamientoRepositoryEnMemoria(DatosDePrueba.alojamiento());
        GestionRecordatoriosService recordatorios =
                new GestionRecordatoriosService(new RecordatorioRepositoryEnMemoria(), reservas, alojamiento);
        servicio = new VencimientoReservasPendientesService(reservas, folios, alojamiento, recordatorios);
    }

    private Reserva pendienteConFolio(String codigo) {
        Reserva reserva = DatosDePrueba.reservaPendiente(codigo, apartamento, estancia(LocalDate.of(2026, 9, 10), 3));
        reservas.con(reserva);
        folios.guardar(Folio.abrir(new CodigoFolio("FOL-" + codigo), reserva.getCodigo(), "Alojamiento",
                reserva.getValorCongelado(), AHORA.toLocalDate()));
        return reserva;
    }

    @Test
    @DisplayName("RN-21: pasado el plazo, la PENDIENTE pasa a CANCELADA, libera sus noches y anula su cargo")
    void cancelarReservaPendienteVencida() {
        Reserva reserva = pendienteConFolio("RES-1");

        List<Reserva> canceladas = servicio.cancelarVencidas(AHORA.plusHours(25));

        assertEquals(1, canceladas.size());
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertFalse(reserva.estaActiva());
        assertFalse(reserva.ocupa(new Noche(LocalDate.of(2026, 9, 10))));
        assertNotNull(reserva.getMotivoCancelacion());
        Folio folio = folios.obtenerPorReserva(reserva.getCodigo()).orElseThrow();
        assertTrue(folio.totalCargos().esCero());
    }

    @Test
    @DisplayName("RN-21: dentro del plazo (incluso justo en el límite) la reserva sigue PENDIENTE")
    void noCancelarReservaPendienteDentroDelPlazo() {
        Reserva reserva = pendienteConFolio("RES-1");

        assertTrue(servicio.cancelarVencidas(AHORA.plusHours(23)).isEmpty());
        assertTrue(servicio.cancelarVencidas(AHORA.plusHours(24)).isEmpty());

        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertTrue(reserva.estaActiva());
    }

    @Test
    @DisplayName("RN-21: solo vencen las PENDIENTES; una CONFIRMADA no se toca aunque pase el plazo")
    void noCancelarReservaConfirmadaAunqueHayaPasadoElPlazo() {
        Reserva confirmada = DatosDePrueba.reservaConfirmada("RES-C", apartamento,
                estancia(LocalDate.of(2026, 9, 20), 2));
        reservas.con(confirmada);

        List<Reserva> canceladas = servicio.cancelarVencidas(AHORA.plusDays(10));

        assertTrue(canceladas.isEmpty());
        assertEquals(EstadoReserva.CONFIRMADA, confirmada.getEstado());
    }
}
