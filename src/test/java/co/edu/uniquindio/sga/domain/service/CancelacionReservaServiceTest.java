package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.soporte.DatosDePrueba;
import co.edu.uniquindio.sga.domain.soporte.EscenarioReservas;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** RN-13: retención según la política de cancelación cuya versión quedó congelada en la reserva (con RN-22). */
class CancelacionReservaServiceTest {

    private EscenarioReservas e;
    private Reserva reserva;
    private Dinero anticipo;

    @BeforeEach
    void preparar() {
        e = new EscenarioReservas();
        reserva = e.crearReserva();
        anticipo = e.anticipoDe(reserva);
    }

    private void pagarAnticipo(Dinero monto) {
        e.pagos.registrarPago(reserva.getCodigo(), monto, MedioPago.TRANSFERENCIA, DatosDePrueba.HOY);
    }

    private LocalDateTime faltando(int dias) {
        return EscenarioReservas.ENTRADA.minusDays(dias).atTime(9, 0);
    }

    private Folio folio() {
        return e.folios.obtenerPorReserva(reserva.getCodigo()).orElseThrow();
    }

    @Test
    @DisplayName("RN-13: con antelación en el primer tramo no se retiene nada")
    void sinRetencionConMuchaAntelacion() {
        pagarAnticipo(anticipo);
        LiquidacionPolitica liquidacion = e.cancelacion.cancelar(reserva.getCodigo(), new MotivoCancelacion("Cambio de planes"), faltando(20));
        assertTrue(liquidacion.retencion().esCero());
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
    }

    @Test
    @DisplayName("RN-13: tramo intermedio retiene el 50 % del anticipo pagado")
    void retencionDelTramoIntermedio() {
        pagarAnticipo(anticipo);
        LiquidacionPolitica liquidacion = e.cancelacion.cancelar(reserva.getCodigo(), new MotivoCancelacion("Cambio de planes"), faltando(10));
        assertEquals(new Porcentaje(50).aplicarA(anticipo), liquidacion.retencion());
    }

    @Test
    @DisplayName("RN-13: con poca antelación retiene el 100 % del anticipo")
    void retencionTotalConPocaAntelacion() {
        pagarAnticipo(anticipo);
        LiquidacionPolitica liquidacion = e.cancelacion.cancelar(reserva.getCodigo(), new MotivoCancelacion("Cambio de planes"), faltando(2));
        assertEquals(anticipo, liquidacion.retencion());
    }

    @Test
    @DisplayName("RN-13: la retención se calcula sobre lo realmente pagado si es menos que el anticipo")
    void baseLimitadaAlPagado() {
        Dinero parcial = new Porcentaje(50).aplicarA(anticipo);
        pagarAnticipo(parcial);
        LiquidacionPolitica liquidacion = e.cancelacion.cancelar(reserva.getCodigo(), new MotivoCancelacion("Cambio de planes"), faltando(2));
        assertEquals(parcial, liquidacion.retencion());
    }

    @Test
    @DisplayName("RN-13: sin pagos no hay nada que retener")
    void sinPagosNoRetiene() {
        LiquidacionPolitica liquidacion = e.cancelacion.cancelar(reserva.getCodigo(), new MotivoCancelacion("Cambio de planes"), faltando(2));
        assertTrue(liquidacion.retencion().esCero());
    }

    @Test
    @DisplayName("RN-13/RN-22: se aplica la versión congelada, no la política vigente al cancelar")
    void usaLaVersionCongelada() {
        pagarAnticipo(anticipo);
        PoliticaCancelacion v1 = e.politicas.obtenerVigente();
        PoliticaCancelacion v2 = v1.publicarNuevaVersion(
                List.of(new TramoCancelacion(7, Porcentaje.CIEN), new TramoCancelacion(0, Porcentaje.CIEN)),
                Porcentaje.CIEN, DatosDePrueba.AHORA.plusDays(1));
        e.politicas.guardar(v1);
        e.politicas.guardar(v2);
        assertEquals(new VersionPolitica(1), reserva.getPoliticaVersionCongelada());
        assertEquals(new VersionPolitica(2), e.politicas.obtenerVigente().getVersion());

        LiquidacionPolitica liquidacion = e.cancelacion.cancelar(reserva.getCodigo(), new MotivoCancelacion("Cambio de planes"), faltando(10));

        assertEquals(new Porcentaje(50).aplicarA(anticipo), liquidacion.retencion(), "Debe aplicar v1 (50 %), no v2 (100 %)");
    }

    @Test
    @DisplayName("RN-13: una reserva creada después de publicar la nueva versión congela la nueva")
    void reservaNuevaCongelaLaNuevaVersion() {
        PoliticaCancelacion v1 = e.politicas.obtenerVigente();
        PoliticaCancelacion v2 = v1.publicarNuevaVersion(
                List.of(new TramoCancelacion(3, Porcentaje.CERO), new TramoCancelacion(0, Porcentaje.CIEN)),
                Porcentaje.CIEN, DatosDePrueba.AHORA);
        e.politicas.guardar(v1);
        e.politicas.guardar(v2);
        Reserva otra = e.creacion.crear(DatosDePrueba.titular(), EscenarioReservas.APTO,
                DatosDePrueba.estancia(LocalDate.of(2026, 10, 20), 2), List.of(), java.time.LocalTime.of(15, 0),
                CanalOrigen.PORTAL, null, DatosDePrueba.AHORA);
        assertEquals(new VersionPolitica(2), otra.getPoliticaVersionCongelada());
    }

    @Test
    @DisplayName("RN-13: el cargo de penalidad queda en el folio y el saldo lo refleja")
    void laRetencionQuedaEnElFolio() {
        pagarAnticipo(anticipo);
        LiquidacionPolitica liquidacion = e.cancelacion.cancelar(reserva.getCodigo(), new MotivoCancelacion("Cambio de planes"), faltando(2));
        assertTrue(folio().getCargos().stream().anyMatch(c -> c.tipo() == TipoCargo.PENALIDAD));
        assertEquals(folio().calcularSaldo(), liquidacion.saldoFolio());
    }

    @Test
    @DisplayName("RN-13 (no-show): retiene el porcentaje de no-show de la política congelada")
    void noShowUsaLaVersionCongelada() {
        reserva.confirmar();
        e.reservas.guardar(reserva);
        pagarAnticipo(anticipo);
        PoliticaCancelacion v1 = e.politicas.obtenerVigente();
        PoliticaCancelacion v2 = v1.publicarNuevaVersion(
                List.of(new TramoCancelacion(7, Porcentaje.CERO), new TramoCancelacion(0, Porcentaje.CERO)),
                Porcentaje.CERO, DatosDePrueba.AHORA.plusDays(1));
        e.politicas.guardar(v1);
        e.politicas.guardar(v2);

        LiquidacionPolitica liquidacion = e.noShow.declararNoShow(reserva.getCodigo(),
                EscenarioReservas.ENTRADA.atTime(19, 0));

        assertEquals(anticipo, liquidacion.retencion(), "v1 retiene 100 %; v2 (0 %) no debe aplicarse");
        assertEquals(EstadoReserva.NO_SHOW, reserva.getEstado());
    }

    @Test
    @DisplayName("RN-13: la política debe existir para la versión congelada")
    void rechazaSiNoExisteLaVersion() {
        EscenarioReservas otro = new EscenarioReservas();
        Reserva r = otro.crearReserva();
        CancelacionReservaService servicio = new CancelacionReservaService(otro.reservas, otro.folios,
                new co.edu.uniquindio.sga.domain.soporte.PoliticaCancelacionRepositoryEnMemoria(),
                otro.alojamientos, new GestionRecordatoriosService(
                        new co.edu.uniquindio.sga.domain.soporte.RecordatorioRepositoryEnMemoria(), otro.reservas, otro.alojamientos));
        assertThrows(ReglaDominioException.class,
                () -> servicio.cancelar(r.getCodigo(), new MotivoCancelacion("x"), faltando(2)));
    }

    @Test
    @DisplayName("Cancelar una reserva inexistente se rechaza")
    void rechazaReservaInexistente() {
        assertThrows(ReglaDominioException.class,
                () -> e.cancelacion.cancelar(new CodigoReserva("NO-EXISTE"), new MotivoCancelacion("x"), faltando(2)));
    }

    @Test
    @DisplayName("RN-08 con RN-13: no se cancela dos veces ni se liquida dos veces")
    void noSeCancelaDosVeces() {
        pagarAnticipo(anticipo);
        e.cancelacion.cancelar(reserva.getCodigo(), new MotivoCancelacion("x"), faltando(10));
        assertThrows(ReglaDominioException.class,
                () -> e.cancelacion.cancelar(reserva.getCodigo(), new MotivoCancelacion("x"), faltando(10)));
    }
}
