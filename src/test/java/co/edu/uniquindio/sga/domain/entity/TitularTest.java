package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.titular;
import static org.junit.jupiter.api.Assertions.*;

/** El titular siempre es un ocupante y debe tener correo válido (recibe los recordatorios). */
class TitularTest {

    private static final LocalDate NACIMIENTO = LocalDate.of(1990, 5, 5);

    @Test
    @DisplayName("Un titular válido se convierte en ocupante con su nombre y fecha de nacimiento")
    void convertirseEnOcupante() {
        Titular titular = titular();

        assertEquals("Ana Gómez", titular.comoOcupante().nombre());
        assertEquals(NACIMIENTO, titular.comoOcupante().fechaNacimiento());
    }

    @Test
    @DisplayName("El titular exige código, nombre, fecha de nacimiento, teléfono y correo válido")
    void exigirDatosObligatorios() {
        CodigoTitular codigo = new CodigoTitular("T-1");

        assertThrows(ReglaDominioException.class, () -> new Titular(null, "Ana", NACIMIENTO, "300", "a@b.co"));
        assertThrows(ReglaDominioException.class, () -> new Titular(codigo, " ", NACIMIENTO, "300", "a@b.co"));
        assertThrows(ReglaDominioException.class, () -> new Titular(codigo, "Ana", null, "300", "a@b.co"));
        assertThrows(ReglaDominioException.class, () -> new Titular(codigo, "Ana", NACIMIENTO, " ", "a@b.co"));
        assertThrows(ReglaDominioException.class, () -> new Titular(codigo, "Ana", NACIMIENTO, "300", "sin-arroba"));
        assertThrows(ReglaDominioException.class, () -> new Titular(codigo, "Ana", NACIMIENTO, "300", null));
    }

    @Test
    @DisplayName("Actualizar el contacto mantiene las mismas exigencias")
    void noActualizarConContactoInvalido() {
        Titular titular = titular();

        assertThrows(ReglaDominioException.class, () -> titular.actualizarContacto("300", "correo-malo"));
        assertEquals("ana@correo.com", titular.getCorreo());
        titular.actualizarContacto("3110000000", "nuevo@correo.com");
        assertEquals("nuevo@correo.com", titular.getCorreo());
    }
}
