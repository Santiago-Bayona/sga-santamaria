package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** RN-03: la salida es posterior a la entrada (toda estancia tiene al menos una noche); 3.1: rango [entrada, salida). */
class EstanciaTest {

    private static final LocalDate DIA_10 = LocalDate.of(2026, 9, 10);

    @Test
    @DisplayName("RN-03: una estancia con salida posterior a la entrada es válida")
    void crearEstanciaConSalidaPosteriorALaEntrada() {
        Estancia estancia = new Estancia(DIA_10, DIA_10.plusDays(3));

        assertEquals(3, estancia.numeroNoches());
        assertEquals(3, estancia.noches().size());
        assertEquals(new Noche(DIA_10), estancia.noches().get(0));
    }

    @Test
    @DisplayName("RN-03: la estancia mínima es de una noche")
    void admitirEstanciaDeUnaNoche() {
        assertEquals(1, new Estancia(DIA_10, DIA_10.plusDays(1)).numeroNoches());
    }

    @Test
    @DisplayName("RN-03: salida igual a la entrada se rechaza")
    void noCrearEstanciaConSalidaIgualALaEntrada() {
        assertThrows(ReglaDominioException.class, () -> new Estancia(DIA_10, DIA_10));
    }

    @Test
    @DisplayName("RN-03: salida anterior a la entrada se rechaza")
    void noCrearEstanciaConSalidaAnteriorALaEntrada() {
        assertThrows(ReglaDominioException.class, () -> new Estancia(DIA_10, DIA_10.minusDays(1)));
    }

    @Test
    @DisplayName("RN-03: fechas nulas se rechazan")
    void noCrearEstanciaSinFechas() {
        assertThrows(ReglaDominioException.class, () -> new Estancia(null, DIA_10));
        assertThrows(ReglaDominioException.class, () -> new Estancia(DIA_10, null));
    }

    @Test
    @DisplayName("3.1: entrada_A < salida_B y entrada_B < salida_A; el día de salida no cuenta como noche ocupada")
    void detectarSolapamientoSegunElRangoSemiabierto() {
        Estancia a = new Estancia(DIA_10, DIA_10.plusDays(3));                       // 10 → 13

        assertTrue(a.seSolapaCon(new Estancia(DIA_10.plusDays(2), DIA_10.plusDays(5))));
        assertTrue(a.seSolapaCon(new Estancia(DIA_10.minusDays(1), DIA_10.plusDays(1))));
        assertTrue(a.seSolapaCon(new Estancia(DIA_10.plusDays(1), DIA_10.plusDays(2))));   // contenida
        assertFalse(a.seSolapaCon(new Estancia(DIA_10.plusDays(3), DIA_10.plusDays(5))));  // empieza el día de salida
        assertFalse(a.seSolapaCon(new Estancia(DIA_10.minusDays(2), DIA_10)));             // termina el día de entrada
        assertFalse(a.contiene(new Noche(DIA_10.plusDays(3))));
    }
}
