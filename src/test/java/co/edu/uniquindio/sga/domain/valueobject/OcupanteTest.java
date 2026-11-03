package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** RN-06 (facturable según el umbral de edad, fijado a la fecha de entrada) y 3.2 (la edad se calcula, no se guarda). */
class OcupanteTest {

    private static final UmbralEdadFacturable UMBRAL = new UmbralEdadFacturable(5);
    private static final LocalDate ENTRADA = LocalDate.of(2026, 9, 10);

    @Test
    @DisplayName("RN-06: quien tiene la edad del umbral o más es facturable")
    void determinarSiEsFacturableSegunEdad() {
        Ocupante adulto = new Ocupante("Luis", LocalDate.of(1985, 3, 3));
        Ocupante justoEnElUmbral = new Ocupante("Sara", ENTRADA.minusYears(5));      // cumple 5 el día de entrada

        assertTrue(adulto.esFacturableEn(ENTRADA, UMBRAL));
        assertTrue(justoEnElUmbral.esFacturableEn(ENTRADA, UMBRAL));
    }

    @Test
    @DisplayName("RN-06: quien no alcanza el umbral no es facturable (un día antes de cumplirlo)")
    void noFacturarAQuienNoAlcanzaElUmbral() {
        Ocupante aunNoCumple = new Ocupante("Pedro", ENTRADA.minusYears(5).plusDays(1));   // 4 años y 364 días
        Ocupante bebe = new Ocupante("Sofía", ENTRADA.minusMonths(8));

        assertEquals(4, aunNoCumple.edadEn(ENTRADA));
        assertFalse(aunNoCumple.esFacturableEn(ENTRADA, UMBRAL));
        assertFalse(bebe.esFacturableEn(ENTRADA, UMBRAL));
    }

    @Test
    @DisplayName("RN-06: la condición se evalúa a la fecha de entrada; no cambia si cumple años durante la estancia")
    void noCambiarLaCondicionDuranteLaEstancia() {
        Ocupante cumpleDuranteLaEstancia = new Ocupante("Pedro", ENTRADA.minusYears(5).plusDays(2));

        assertFalse(cumpleDuranteLaEstancia.esFacturableEn(ENTRADA, UMBRAL));
        assertTrue(cumpleDuranteLaEstancia.edadEn(ENTRADA.plusDays(2)) >= 5);
    }

    @Test
    @DisplayName("RN-06: el umbral es configurable; con otro umbral cambia el resultado")
    void respetarElUmbralConfigurado() {
        Ocupante nino = new Ocupante("Pedro", ENTRADA.minusYears(3));

        assertFalse(nino.esFacturableEn(ENTRADA, new UmbralEdadFacturable(5)));
        assertTrue(nino.esFacturableEn(ENTRADA, new UmbralEdadFacturable(3)));
        assertThrows(ReglaDominioException.class, () -> new UmbralEdadFacturable(-1));
    }

    @Test
    @DisplayName("3.2: el ocupante exige nombre y fecha de nacimiento; la edad se calcula y no admite fechas anteriores al nacimiento")
    void validarDatosDelOcupante() {
        assertThrows(ReglaDominioException.class, () -> new Ocupante(" ", LocalDate.of(2000, 1, 1)));
        assertThrows(ReglaDominioException.class, () -> new Ocupante("Luis", null));
        Ocupante luis = new Ocupante("Luis", LocalDate.of(2000, 1, 1));
        assertThrows(ReglaDominioException.class, () -> luis.edadEn(LocalDate.of(1999, 12, 31)));
    }
}
