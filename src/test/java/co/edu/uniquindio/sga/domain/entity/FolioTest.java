package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** RN-17 (cierre del folio) y RN-16 (cargos y pagos inmodificables; se corrige con movimiento inverso). */
class FolioTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 9, 10);
    private static final Dinero ALOJAMIENTO = Dinero.pesos(300_000);

    private Folio folio;

    @BeforeEach
    void preparar() {
        folio = Folio.abrir(new CodigoFolio("FOL-1"), new CodigoReserva("RES-1"), "Alojamiento 3 noches",
                ALOJAMIENTO, FECHA);
    }

    //  RN-17

    @Test
    @DisplayName("RN-17: no cierra con saldo pendiente y sigue abierto")
    void noCerrarFolioConSaldoPendiente() {
        assertTrue(folio.calcularSaldo().debeHuesped());

        assertThrows(ReglaDominioException.class, folio::cerrar);
        assertFalse(folio.estaCerrado());
    }

    @Test
    @DisplayName("RN-17: tampoco cierra con saldo a favor (distinto de cero)")
    void noCerrarFolioConSaldoAFavor() {
        folio.registrarPago(Dinero.pesos(350_000), MedioPago.EFECTIVO, FECHA);

        assertTrue(folio.calcularSaldo().esAFavor());
        assertThrows(ReglaDominioException.class, folio::cerrar);
    }

    @Test
    @DisplayName("RN-17: con saldo cero se cierra sin necesidad de autorización")
    void cerrarFolioConSaldoCero() {
        folio.registrarPago(ALOJAMIENTO, MedioPago.EFECTIVO, FECHA);

        folio.cerrar();

        assertTrue(folio.estaCerrado());
        assertNull(folio.getAutorizacionCierre());
    }

    @Test
    @DisplayName("RN-17: con autorización explícita (autor y motivo) se cierra aunque haya saldo")
    void cerrarFolioConSaldoPendienteConAutorizacion() {
        AutorizacionCierre autorizacion = new AutorizacionCierre("Gerente", "Cortesía al huésped");

        folio.cerrarConAutorizacion(autorizacion);

        assertTrue(folio.estaCerrado());
        assertEquals(autorizacion, folio.getAutorizacionCierre());
        assertThrows(ReglaDominioException.class, () -> new AutorizacionCierre("Gerente", " "));
        assertThrows(ReglaDominioException.class, () -> new AutorizacionCierre(null, "Motivo"));
    }

    @Test
    @DisplayName("RN-17: un folio cerrado no admite más movimientos")
    void noRegistrarMovimientosEnFolioCerrado() {
        folio.cerrarConAutorizacion(new AutorizacionCierre("Gerente", "Cortesía"));

        assertThrows(ReglaDominioException.class,
                () -> folio.registrarPago(Dinero.pesos(1000), MedioPago.EFECTIVO, FECHA));
        assertThrows(ReglaDominioException.class, () -> folio.revertirCargo(1, FECHA));
    }

    //  RN-16

    @Test
    @DisplayName("RN-16: corregir un cargo agrega un reverso; el original queda intacto")
    void corregirCargoMedianteMovimientoInverso() {
        Cargo original = folio.getCargos().get(0);

        Cargo reverso = folio.revertirCargo(1, FECHA);

        assertEquals(2, folio.getCargos().size());
        assertEquals(original, folio.getCargos().get(0));
        assertTrue(reverso.esReversoDe(original));
        assertEquals(2, reverso.numero());
        assertTrue(folio.cargoRevertido(1));
        assertTrue(folio.totalCargos().esCero());
    }

    @Test
    @DisplayName("RN-16: corregir un pago agrega un reverso y el saldo vuelve a reflejar lo realmente pagado")
    void corregirPagoMedianteMovimientoInverso() {
        folio.registrarPago(ALOJAMIENTO, MedioPago.EFECTIVO, FECHA);

        Pago reverso = folio.revertirPago(1, FECHA);

        assertEquals(2, folio.getPagos().size());
        assertTrue(reverso.esReverso());
        assertTrue(folio.pagoRevertido(1));
        assertTrue(folio.totalPagos().esCero());
        assertTrue(folio.calcularSaldo().debeHuesped());
    }

    @Test
    @DisplayName("RN-16: un movimiento se revierte a lo sumo una vez y no existen reversos de reversos")
    void noRevertirDosVecesElMismoMovimiento() {
        folio.revertirCargo(1, FECHA);

        assertThrows(ReglaDominioException.class, () -> folio.revertirCargo(1, FECHA));
        assertThrows(ReglaDominioException.class, () -> folio.revertirCargo(2, FECHA));
    }

    @Test
    @DisplayName("RN-16: no se corrige un movimiento que no existe ni con fecha anterior al original")
    void noRevertirMovimientoInexistenteOConFechaAnterior() {
        folio.registrarPago(Dinero.pesos(50_000), MedioPago.EFECTIVO, FECHA);

        assertThrows(ReglaDominioException.class, () -> folio.revertirCargo(9, FECHA));
        assertThrows(ReglaDominioException.class, () -> folio.revertirPago(9, FECHA));
        assertThrows(ReglaDominioException.class, () -> folio.revertirPago(1, FECHA.minusDays(1)));
    }

    @Test
    @DisplayName("RN-16: las listas de cargos y pagos son de solo lectura: nada se edita ni se borra desde fuera")
    void exponerCargosYPagosSoloDeLectura() {
        folio.registrarPago(Dinero.pesos(50_000), MedioPago.EFECTIVO, FECHA);

        assertThrows(UnsupportedOperationException.class, () -> folio.getCargos().remove(0));
        assertThrows(UnsupportedOperationException.class, () -> folio.getPagos().clear());
    }
}
