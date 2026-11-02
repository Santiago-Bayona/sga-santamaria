package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.*;
import static org.junit.jupiter.api.Assertions.*;

/** Temporadas del alojamiento (una base, mínimo dos especiales, sin solaparse; toda fecha tiene temporada) y RN-06. */
class AlojamientoTest {

    private Alojamiento alojamiento;

    @BeforeEach
    void preparar() {
        alojamiento = alojamiento();
    }

    private Alojamiento con(List<Temporada> temporadas) {
        return new Alojamiento(new CodigoAlojamiento("A"), "Nombre", "Descripción",
                new Ubicacion("Armenia", "Calle 1", new BigDecimal("4.5"), new BigDecimal("-75.6")),
                configuracion(), temporadas);
    }

    private static Temporada especial(String id, int mesIni, int diaIni, int mesFin, int diaFin) {
        return Temporada.especial(new IdTemporada(id), id,
                List.of(new PeriodoAnual(MonthDay.of(mesIni, diaIni), MonthDay.of(mesFin, diaFin))));
    }

    @Test
    @DisplayName("Toda fecha tiene temporada: la especial que la cubre o, si no, la base (incluye el cruce de año)")
    void obtenerTemporadaDeUnaFecha() {
        assertEquals(TEMPORADA_MEDIA, alojamiento.obtenerTemporada(LocalDate.of(2026, 7, 15)).getId());
        assertEquals(TEMPORADA_ALTA, alojamiento.obtenerTemporada(LocalDate.of(2026, 12, 31)).getId());
        assertEquals(TEMPORADA_ALTA, alojamiento.obtenerTemporada(LocalDate.of(2027, 1, 5)).getId());
        assertEquals(TEMPORADA_BASE, alojamiento.obtenerTemporada(LocalDate.of(2026, 9, 10)).getId());
    }

    @Test
    @DisplayName("Debe existir exactamente una temporada base")
    void exigirUnaUnicaTemporadaBase() {
        Temporada e1 = especial("E1", 1, 1, 1, 31);
        Temporada e2 = especial("E2", 3, 1, 3, 31);

        assertThrows(ReglaDominioException.class, () -> con(List.of(e1, e2)));
        assertThrows(ReglaDominioException.class, () -> con(List.of(
                Temporada.base(new IdTemporada("B1"), "B1"), Temporada.base(new IdTemporada("B2"), "B2"), e1, e2)));
        assertThrows(ReglaDominioException.class,
                () -> alojamiento.agregarTemporada(Temporada.base(new IdTemporada("B2"), "Otra base")));
    }

    @Test
    @DisplayName("Debe haber al menos dos temporadas especiales además de la base")
    void exigirAlMenosDosTemporadasEspeciales() {
        Temporada base = Temporada.base(new IdTemporada("B"), "Base");

        assertThrows(ReglaDominioException.class, () -> con(List.of(base, especial("E1", 1, 1, 1, 31))));
        assertEquals(3, con(List.of(base, especial("E1", 1, 1, 1, 31), especial("E2", 3, 1, 3, 31)))
                .getTemporadas().size());
    }

    @Test
    @DisplayName("Las temporadas especiales no pueden solaparse entre sí ni repetir identificador")
    void noPermitirTemporadasSolapadas() {
        Temporada base = Temporada.base(new IdTemporada("B"), "Base");

        assertThrows(ReglaDominioException.class, () -> con(List.of(base,
                especial("E1", 1, 1, 1, 31), especial("E2", 1, 15, 2, 15))));
        assertThrows(ReglaDominioException.class, () -> alojamiento.agregarTemporada(especial("NUEVA", 7, 20, 8, 10)));
        assertEquals(3, alojamiento.getTemporadas().size());           // la inválida no se agregó
    }

    @Test
    @DisplayName("La temporada base no se retira y una especial no puede dejar al alojamiento con menos de dos")
    void noRetirarTemporadasObligatorias() {
        assertThrows(ReglaDominioException.class, () -> alojamiento.retirarTemporada(TEMPORADA_BASE));
        assertThrows(ReglaDominioException.class, () -> alojamiento.retirarTemporada(TEMPORADA_MEDIA));
        assertThrows(ReglaDominioException.class, () -> alojamiento.retirarTemporada(new IdTemporada("NO-EXISTE")));
    }

    @Test
    @DisplayName("Una temporada especial requiere rangos y sus rangos no se solapan; la base no tiene rangos")
    void validarRangosDeLaTemporada() {
        assertThrows(ReglaDominioException.class,
                () -> Temporada.especial(new IdTemporada("E"), "E", List.of()));
        assertThrows(ReglaDominioException.class, () -> Temporada.especial(new IdTemporada("E"), "E", List.of(
                new PeriodoAnual(MonthDay.of(1, 1), MonthDay.of(1, 20)),
                new PeriodoAnual(MonthDay.of(1, 10), MonthDay.of(1, 30)))));
        assertThrows(ReglaDominioException.class,
                () -> Temporada.base(new IdTemporada("B"), "B").redefinirPeriodos(List.of()));
    }

    @Test
    @DisplayName("RN-06: el alojamiento decide quién es facturable con su umbral configurado")
    void decidirFacturableConElUmbralDelAlojamiento() {
        LocalDate entrada = LocalDate.of(2026, 9, 10);

        assertTrue(alojamiento.esFacturable(conEdadEn("Sara", 5, entrada), entrada));
        assertFalse(alojamiento.esFacturable(conEdadEn("Pedro", 4, entrada), entrada));
    }

    @Test
    @DisplayName("Los servicios adicionales no repiten nombre y el que genera cargo debe tener valor")
    void validarServiciosAdicionales() {
        alojamiento.agregarServicioAdicional(new ServicioAdicional("Desayuno", Dinero.pesos(20_000), true));

        assertThrows(ReglaDominioException.class,
                () -> alojamiento.agregarServicioAdicional(new ServicioAdicional("Desayuno", Dinero.pesos(1), true)));
        assertThrows(ReglaDominioException.class, () -> new ServicioAdicional("Gratis", Dinero.CERO, true));
        assertEquals(Dinero.CERO, new ServicioAdicional("Cortesía", Dinero.pesos(5000), false).costo());
    }
}
