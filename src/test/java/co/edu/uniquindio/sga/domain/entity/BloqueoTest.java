package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.AHORA;
import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.estancia;
import static org.junit.jupiter.api.Assertions.*;

/** RN-07: un bloqueo vigente sobre una noche la deja no disponible; levantarlo la libera. */
class BloqueoTest {

    private static final LocalDate DIA = LocalDate.of(2026, 9, 10);
    private Bloqueo bloqueo;

    @BeforeEach
    void preparar() {
        bloqueo = new Bloqueo(new CodigoBloqueo("BLQ-1"), new IdentificacionApartamento("APT-301"),
                estancia(DIA, 3), new MotivoBloqueo("Mantenimiento"), AHORA);
    }

    @Test
    @DisplayName("RN-07: un bloqueo nace vigente y bloquea las noches de su rango (la de salida no)")
    void bloquearLasNochesDelRango() {
        assertTrue(bloqueo.estaVigente());
        assertTrue(bloqueo.bloqueaNoche(new Noche(DIA)));
        assertTrue(bloqueo.bloqueaNoche(new Noche(DIA.plusDays(2))));
        assertFalse(bloqueo.bloqueaNoche(new Noche(DIA.plusDays(3))));
        assertTrue(bloqueo.seSolapaCon(estancia(DIA.plusDays(2), 2)));
        assertFalse(bloqueo.seSolapaCon(estancia(DIA.plusDays(3), 2)));
    }

    @Test
    @DisplayName("RN-07: levantado deja de bloquear y no admite más cambios ni se levanta dos veces")
    void liberarLasNochesAlLevantar() {
        bloqueo.levantar();

        assertFalse(bloqueo.estaVigente());
        assertFalse(bloqueo.bloqueaNoche(new Noche(DIA)));
        assertFalse(bloqueo.seSolapaCon(estancia(DIA, 3)));
        assertThrows(ReglaDominioException.class, bloqueo::levantar);
        assertThrows(ReglaDominioException.class, () -> bloqueo.reprogramar(estancia(DIA, 1)));
    }

    @Test
    @DisplayName("RN-07: el bloqueo exige todos sus datos (código, apartamento, rango, motivo y fecha)")
    void noCrearBloqueoIncompleto() {
        assertThrows(ReglaDominioException.class, () -> new Bloqueo(new CodigoBloqueo("B"),
                new IdentificacionApartamento("APT-301"), null, new MotivoBloqueo("x"), AHORA));
        assertThrows(ReglaDominioException.class, () -> new MotivoBloqueo(" "));
    }
}
