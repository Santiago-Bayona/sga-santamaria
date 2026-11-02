package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** 7.4 (valor por ocupante facturable por noche): el objeto Cotizacion y su desglose noche por noche. */
class CotizacionTest {

    private static final IdTemporada BASE = new IdTemporada("BASE");
    private static final LocalDate DIA = LocalDate.of(2026, 9, 10);

    private static DesgloseNoche noche(int diasDespues, long tarifa, int facturables) {
        return DesgloseNoche.calcular(new Noche(DIA.plusDays(diasDespues)),
                new Tarifa(BASE, Dinero.pesos(tarifa)), facturables);
    }

    @Test
    @DisplayName("7.4: el subtotal de cada noche es tarifa × ocupantes facturables y el total suma las noches")
    void definirOcupanteFacturableMultiplicaLaTarifaPorNoche() {
        Cotizacion cotizacion = new Cotizacion(List.of(noche(0, 100_000, 2), noche(1, 100_000, 2), noche(2, 150_000, 2)));

        assertEquals(3, cotizacion.numeroNoches());
        assertEquals(Dinero.pesos(200_000), cotizacion.desglose().get(0).subtotal());
        assertEquals(Dinero.pesos(700_000), cotizacion.total());
    }

    @Test
    @DisplayName("7.4: el desglose rechaza un subtotal que no sea tarifa × ocupantes facturables")
    void noAceptarSubtotalInconsistente() {
        assertThrows(ReglaDominioException.class, () -> new DesgloseNoche(new Noche(DIA), BASE,
                Dinero.pesos(100_000), 2, Dinero.pesos(150_000)));
    }

    @Test
    @DisplayName("7.4: toda cotización cubre al menos una noche")
    void noCrearCotizacionSinNoches() {
        assertThrows(ReglaDominioException.class, () -> new Cotizacion(List.of()));
        assertThrows(ReglaDominioException.class, () -> new Cotizacion(null));
    }

    @Test
    @DisplayName("RN-14 (apoyo): la diferencia frente a una cotización anterior puede ser positiva o negativa")
    void calcularDiferenciaFrenteAOtraCotizacion() {
        Cotizacion barata = new Cotizacion(List.of(noche(0, 100_000, 1)));
        Cotizacion cara = new Cotizacion(List.of(noche(0, 100_000, 3)));

        assertEquals(200_000, cara.diferenciaFrente(barata).intValue());
        assertEquals(-200_000, barata.diferenciaFrente(cara).intValue());
    }
}
