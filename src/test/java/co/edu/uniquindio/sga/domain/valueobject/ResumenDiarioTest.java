package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/** Invariantes del resumen diario y de lo que cruza el puerto de IA. */
class ResumenDiarioTest {

    private static final DatosResumenDiario DATOS = new DatosResumenDiario(LocalDate.of(2026, 11, 12), List.of(), List.of());

    @Test
    @DisplayName("Un resumen sin IA debe decir por qué")
    void sinIaRequiereMotivo() {
        assertThrows(ReglaDominioException.class, () -> new ResumenDiario(DATOS, "texto", false, null));
    }

    @Test
    @DisplayName("Un resumen generado por IA no puede traer motivo de degradación")
    void conIaNoTieneMotivo() {
        assertThrows(ReglaDominioException.class,
                () -> new ResumenDiario(DATOS, "texto", true, MotivoDegradacionIa.TIEMPO_AGOTADO));
    }

    @Test
    @DisplayName("El resumen requiere datos y texto")
    void requiereDatosYTexto() {
        assertThrows(ReglaDominioException.class,
                () -> new ResumenDiario(null, "texto", true, null));
        assertThrows(ReglaDominioException.class,
                () -> new ResumenDiario(DATOS, " ", true, null));
    }

    @Test
    @DisplayName("El generador no puede devolver un resumen vacío")
    void resumenRedactadoNoVacio() {
        assertThrows(ReglaDominioException.class, () -> new ResumenRedactado("", true));
        assertThrows(ReglaDominioException.class, () -> new ResumenRedactado(null, false));
    }

    @Test
    @DisplayName("Los datos del resumen requieren fecha y ambas listas")
    void datosRequierenFechaYListas() {
        assertThrows(ReglaDominioException.class, () -> new DatosResumenDiario(null, List.of(), List.of()));
        assertThrows(ReglaDominioException.class, () -> new DatosResumenDiario(LocalDate.of(2026, 11, 12), null, List.of()));
        assertThrows(ReglaDominioException.class, () -> new DatosResumenDiario(LocalDate.of(2026, 11, 12), List.of(), null));
    }

    @Test
    @DisplayName("Un movimiento requiere reserva, apartamento y al menos un ocupante")
    void movimientoRequiereLoMinimo() {
        CodigoReserva reserva = new CodigoReserva("RES-1");
        IdentificacionApartamento apto = new IdentificacionApartamento("APT-01");
        assertThrows(ReglaDominioException.class, () -> new MovimientoDiario(null, apto, null, 1));
        assertThrows(ReglaDominioException.class, () -> new MovimientoDiario(reserva, null, null, 1));
        assertThrows(ReglaDominioException.class, () -> new MovimientoDiario(reserva, apto, null, 0));
    }
}
