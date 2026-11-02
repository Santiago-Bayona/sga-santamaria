package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.soporte.DatosDePrueba;
import co.edu.uniquindio.sga.domain.soporte.EscenarioReservas;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** RN-15: los pagos se registran contra el folio con medio y fecha; saldo = cargos - pagos. */
class LiquidacionPagoServiceTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 6, 2);

    private EscenarioReservas e;
    private Reserva reserva;
    private Dinero valor;

    @BeforeEach
    void preparar() {
        e = new EscenarioReservas();
        reserva = e.crearReserva();
        valor = reserva.getValorCongelado();
    }

    private Folio folio() {
        return e.folios.obtenerPorReserva(reserva.getCodigo()).orElseThrow();
    }

    @Test
    @DisplayName("RN-15: el pago queda registrado en el folio con su medio y su fecha")
    void pagoConMedioYFecha() {
        ResultadoMovimientoFolio<Pago> r = e.pagos.registrarPago(reserva.getCodigo(), Dinero.pesos(50_000), MedioPago.TARJETA, FECHA);
        assertEquals(MedioPago.TARJETA, r.movimiento().medio());
        assertEquals(FECHA, r.movimiento().fecha());
        assertEquals(1, folio().getPagos().size());
    }

    @Test
    @DisplayName("RN-15: saldo = cargos - pagos tras un pago parcial")
    void saldoTrasPagoParcial() {
        ResultadoMovimientoFolio<Pago> r = e.pagos.registrarPago(reserva.getCodigo(), Dinero.pesos(50_000), MedioPago.EFECTIVO, FECHA);
        assertEquals(0, valor.restar(Dinero.pesos(50_000)).valor().compareTo(r.saldo().valor()));
        assertTrue(r.saldo().debeHuesped());
    }

    @Test
    @DisplayName("RN-15: pagar todo deja el saldo en cero")
    void saldoCeroAlPagarTodo() {
        ResultadoMovimientoFolio<Pago> r = e.pagos.registrarPago(reserva.getCodigo(), valor, MedioPago.TRANSFERENCIA, FECHA);
        assertTrue(r.saldo().esCero());
    }

    @Test
    @DisplayName("RN-15: pagar de más deja saldo a favor")
    void saldoAFavor() {
        ResultadoMovimientoFolio<Pago> r = e.pagos.registrarPago(reserva.getCodigo(), valor.sumar(Dinero.pesos(10_000)), MedioPago.EFECTIVO, FECHA);
        assertTrue(r.saldo().esAFavor());
    }

    @Test
    @DisplayName("RN-15: varios pagos se acumulan y se numeran en orden")
    void variosPagos() {
        e.pagos.registrarPago(reserva.getCodigo(), Dinero.pesos(10_000), MedioPago.EFECTIVO, FECHA);
        ResultadoMovimientoFolio<Pago> r = e.pagos.registrarPago(reserva.getCodigo(), Dinero.pesos(20_000), MedioPago.TARJETA, FECHA);
        assertEquals(2, r.movimiento().numero());
        assertEquals(0, Dinero.pesos(30_000).valor().compareTo(folio().totalPagos().valor()));
    }

    @Test
    @DisplayName("RN-15: un pago de monto cero se rechaza")
    void rechazaMontoCero() {
        assertThrows(ReglaDominioException.class,
                () -> e.pagos.registrarPago(reserva.getCodigo(), Dinero.CERO, MedioPago.EFECTIVO, FECHA));
    }

    @Test
    @DisplayName("RN-15: el pago exige medio y fecha")
    void rechazaSinMedioOFecha() {
        assertThrows(ReglaDominioException.class,
                () -> e.pagos.registrarPago(reserva.getCodigo(), Dinero.pesos(1_000), null, FECHA));
        assertThrows(ReglaDominioException.class,
                () -> e.pagos.registrarPago(reserva.getCodigo(), Dinero.pesos(1_000), MedioPago.EFECTIVO, null));
    }

    @Test
    @DisplayName("RN-15: no se registra un pago en una reserva sin folio")
    void rechazaSinFolio() {
        assertThrows(ReglaDominioException.class,
                () -> e.pagos.registrarPago(new CodigoReserva("NO-EXISTE"), Dinero.pesos(1_000), MedioPago.EFECTIVO, FECHA));
    }

    @Test
    @DisplayName("RN-15 con RN-17: un folio cerrado no admite más pagos")
    void rechazaFolioCerrado() {
        e.pagos.registrarPago(reserva.getCodigo(), valor, MedioPago.EFECTIVO, FECHA);
        e.pagos.cerrarFolio(reserva.getCodigo(), null);
        assertThrows(ReglaDominioException.class,
                () -> e.pagos.registrarPago(reserva.getCodigo(), Dinero.pesos(1_000), MedioPago.EFECTIVO, FECHA));
    }

    @Test
    @DisplayName("RN-15/RN-16: revertir un pago agrega un reverso y el original se conserva")
    void revertirPagoConservaElOriginal() {
        e.pagos.registrarPago(reserva.getCodigo(), Dinero.pesos(40_000), MedioPago.EFECTIVO, FECHA);
        ResultadoMovimientoFolio<Pago> r = e.pagos.revertirPago(reserva.getCodigo(), 1, FECHA);
        assertEquals(2, folio().getPagos().size());
        assertTrue(r.movimiento().esReverso());
        assertEquals(0, valor.valor().compareTo(r.saldo().valor()));
    }
}
