package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.MovimientoDiario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** Resumen básico (sin IA) que se muestra cuando el modelo está apagado o falla (10.2). */
class ResumenBasicoDiarioTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 11, 12);

    private static MovimientoDiario llegada(String reserva, String apartamento, int personas, LocalTime hora) {
        return new MovimientoDiario(new CodigoReserva(reserva), new IdentificacionApartamento(apartamento), hora, personas);
    }

    @Test
    @DisplayName("Un día sin movimientos dice 0 llegadas y 0 salidas, sin listas")
    void sinMovimientos() {
        String texto = ResumenBasicoDiario.redactar(new DatosResumenDiario(FECHA, List.of(), List.of()));

        assertEquals("Resumen del 12/11/2026: 0 llegadas y 0 salidas previstas.", texto);
    }

    @Test
    @DisplayName("Con una llegada y una salida usa el singular y detalla cada movimiento")
    void unaLlegadaYUnaSalida() {
        DatosResumenDiario datos = new DatosResumenDiario(FECHA,
                List.of(llegada("RES-1", "APT-01", 3, LocalTime.of(15, 30))),
                List.of(llegada("RES-2", "APT-02", 1, null)));

        String texto = ResumenBasicoDiario.redactar(datos);

        assertEquals("Resumen del 12/11/2026: 1 llegada y 1 salida previstas.\n"
                + "Llegadas: RES-1 (APT-01, 3 personas, hacia las 15:30).\n"
                + "Salidas: RES-2 (APT-02, 1 persona).", texto);
    }

    @Test
    @DisplayName("Con varias llegadas las separa con punto y coma y usa el plural")
    void variasLlegadas() {
        DatosResumenDiario datos = new DatosResumenDiario(FECHA,
                List.of(llegada("RES-1", "APT-01", 2, LocalTime.of(14, 0)),
                        llegada("RES-3", "APT-03", 4, LocalTime.of(18, 0))),
                List.of());

        String texto = ResumenBasicoDiario.redactar(datos);

        assertEquals("Resumen del 12/11/2026: 2 llegadas y 0 salidas previstas.\n"
                + "Llegadas: RES-1 (APT-01, 2 personas, hacia las 14:00); RES-3 (APT-03, 4 personas, hacia las 18:00).",
                texto);
        assertFalse(texto.contains("Salidas:"));
    }
}
