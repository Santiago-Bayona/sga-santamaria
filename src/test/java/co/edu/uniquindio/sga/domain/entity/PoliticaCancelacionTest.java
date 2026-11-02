package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.soporte.DatosDePrueba;
import co.edu.uniquindio.sga.domain.soporte.EscenarioReservas;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** RN-13 / RN-22: la política es versionada; cada versión es inmutable y solo la vigente se reemplaza. */
class PoliticaCancelacionTest {

    private final Estancia estancia = DatosDePrueba.estancia(LocalDate.of(2026, 9, 10), 2);
    private final Dinero anticipo = Dinero.pesos(100_000);

    @Test
    @DisplayName("RN-13: elige el tramo según los días de antelación")
    void eligeElTramoSegunLaAntelacion() {
        PoliticaCancelacion politica = EscenarioReservas.politicaV1();
        assertTrue(politica.calcularRetencionPorCancelacion(anticipo, LocalDate.of(2026, 8, 1), estancia).esCero());
        assertEquals(Dinero.pesos(50_000), politica.calcularRetencionPorCancelacion(anticipo, LocalDate.of(2026, 9, 1), estancia));
        assertEquals(anticipo, politica.calcularRetencionPorCancelacion(anticipo, LocalDate.of(2026, 9, 9), estancia));
    }

    @Test
    @DisplayName("RN-13: justo en el límite del tramo aplica el tramo de mayor antelación")
    void limiteDeTramoInclusivo() {
        PoliticaCancelacion politica = EscenarioReservas.politicaV1();
        assertTrue(politica.calcularRetencionPorCancelacion(anticipo, LocalDate.of(2026, 8, 26), estancia).esCero());
    }

    @Test
    @DisplayName("RN-13: la devolución es el anticipo menos la retención")
    void devolucion() {
        PoliticaCancelacion politica = EscenarioReservas.politicaV1();
        assertEquals(Dinero.pesos(50_000), politica.calcularDevolucionPorCancelacion(anticipo, LocalDate.of(2026, 9, 1), estancia));
    }

    @Test
    @DisplayName("RN-13: el no-show retiene el porcentaje definido")
    void retencionNoShow() {
        assertEquals(anticipo, EscenarioReservas.politicaV1().calcularRetencionPorNoShow(anticipo));
    }

    @Test
    @DisplayName("RN-13: exige al menos dos tramos")
    void exigeDosTramos() {
        assertThrows(ReglaDominioException.class, () -> new PoliticaCancelacion(new CodigoPolitica("P"),
                new VersionPolitica(1), List.of(new TramoCancelacion(0, Porcentaje.CIEN)), Porcentaje.CIEN, DatosDePrueba.AHORA));
    }

    @Test
    @DisplayName("RN-13: no admite tramos con la misma antelación")
    void sinTramosRepetidos() {
        assertThrows(ReglaDominioException.class, () -> new PoliticaCancelacion(new CodigoPolitica("P"),
                new VersionPolitica(1), List.of(new TramoCancelacion(0, Porcentaje.CIEN), new TramoCancelacion(0, Porcentaje.CERO)),
                Porcentaje.CIEN, DatosDePrueba.AHORA));
    }

    @Test
    @DisplayName("RN-13: debe existir un tramo desde 0 días")
    void exigeTramoDesdeCero() {
        assertThrows(ReglaDominioException.class, () -> new PoliticaCancelacion(new CodigoPolitica("P"),
                new VersionPolitica(1), List.of(new TramoCancelacion(3, Porcentaje.CIEN), new TramoCancelacion(7, Porcentaje.CERO)),
                Porcentaje.CIEN, DatosDePrueba.AHORA));
    }

    @Test
    @DisplayName("RN-22: publicar una nueva versión incrementa el número y deja la anterior no vigente")
    void publicarNuevaVersion() {
        PoliticaCancelacion v1 = EscenarioReservas.politicaV1();
        PoliticaCancelacion v2 = v1.publicarNuevaVersion(
                List.of(new TramoCancelacion(5, Porcentaje.CERO), new TramoCancelacion(0, Porcentaje.CIEN)),
                Porcentaje.CERO, DatosDePrueba.AHORA);
        assertEquals(new VersionPolitica(2), v2.getVersion());
        assertTrue(v2.estaVigente());
        assertFalse(v1.estaVigente());
        // la versión 1 conserva sus reglas originales
        assertEquals(3, v1.getTramos().size());
        assertEquals(anticipo, v1.calcularRetencionPorNoShow(anticipo));
    }

    @Test
    @DisplayName("RN-22: solo la versión vigente puede actualizarse")
    void soloLaVigenteSeActualiza() {
        PoliticaCancelacion v1 = EscenarioReservas.politicaV1();
        List<TramoCancelacion> tramos = List.of(new TramoCancelacion(5, Porcentaje.CERO), new TramoCancelacion(0, Porcentaje.CIEN));
        v1.publicarNuevaVersion(tramos, Porcentaje.CERO, DatosDePrueba.AHORA);
        assertThrows(ReglaDominioException.class, () -> v1.publicarNuevaVersion(tramos, Porcentaje.CERO, DatosDePrueba.AHORA));
    }
}
