package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.soporte.DatosDePrueba;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** L-04: la identificación del apartamento es única y estable (igualdad por valor, normalizada, inmutable). */
class IdentificacionApartamentoTest {

    @Test
    @DisplayName("L-04: dos identificaciones con el mismo valor son iguales (unicidad por valor)")
    void dosIdentificacionesConElMismoValorSonIguales() {
        IdentificacionApartamento a = new IdentificacionApartamento("APT-301");
        IdentificacionApartamento b = new IdentificacionApartamento("APT-301");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new IdentificacionApartamento("APT-302"));
    }

    @Test
    @DisplayName("L-04: el valor se normaliza (sin espacios y en mayúsculas) para que no haya duplicados disfrazados")
    void normalizarAMayusculasYSinEspacios() {
        IdentificacionApartamento a = new IdentificacionApartamento("  apt-301 ");

        assertEquals("APT-301", a.valor());
        assertEquals(new IdentificacionApartamento("APT-301"), a);
    }

    @Test
    @DisplayName("L-04: la identificación es obligatoria")
    void noAceptarIdentificacionVacia() {
        assertThrows(ReglaDominioException.class, () -> new IdentificacionApartamento(null));
        assertThrows(ReglaDominioException.class, () -> new IdentificacionApartamento(""));
        assertThrows(ReglaDominioException.class, () -> new IdentificacionApartamento("   "));
    }

    @Test
    @DisplayName("L-04: es estable: modificar los demás datos del apartamento no cambia su identificación")
    void mantenerLaIdentificacionAlActualizarElApartamento() {
        Apartamento apartamento = DatosDePrueba.apartamento("APT-301", 4);
        IdentificacionApartamento antes = apartamento.getIdentificacion();

        apartamento.actualizarDatos("Nuevo nombre", "Nueva descripción", 3);
        apartamento.cambiarCapacidad(new Capacidad(6));

        assertEquals(antes, apartamento.getIdentificacion());
        assertEquals("APT-301", apartamento.getIdentificacion().valor());
    }
}
