package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Dinero en COP: no negativo y sin decimales (se redondea una sola vez al final del cálculo); Saldo; Porcentaje. */
class DineroTest {

    @Test
    @DisplayName("El dinero no puede ser negativo ni tener decimales")
    void validarElValorMonetario() {
        assertThrows(ReglaDominioException.class, () -> Dinero.pesos(-1));
        assertThrows(ReglaDominioException.class, () -> new Dinero(new BigDecimal("100.50")));
        assertThrows(ReglaDominioException.class, () -> new Dinero(null));
        assertEquals(Dinero.pesos(100), new Dinero(new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Redondear es explícito (mitad hacia arriba) y restar más de lo que hay es inválido")
    void redondearYRestar() {
        assertEquals(Dinero.pesos(101), Dinero.redondeado(new BigDecimal("100.5")));
        assertEquals(Dinero.pesos(100), Dinero.redondeado(new BigDecimal("100.4")));
        assertEquals(Dinero.pesos(70), Dinero.pesos(100).restar(Dinero.pesos(30)));
        assertThrows(ReglaDominioException.class, () -> Dinero.pesos(30).restar(Dinero.pesos(100)));
    }

    @Test
    @DisplayName("El saldo es cargos − pagos: debe el huésped, cero o a favor")
    void calcularSaldo() {
        assertTrue(Saldo.entre(Dinero.pesos(300), Dinero.pesos(100)).debeHuesped());
        assertTrue(Saldo.entre(Dinero.pesos(300), Dinero.pesos(300)).esCero());
        assertTrue(Saldo.entre(Dinero.pesos(300), Dinero.pesos(400)).esAFavor());
    }

    @Test
    @DisplayName("El porcentaje está entre 0 y 100 y se aplica con redondeo a pesos enteros")
    void aplicarPorcentaje() {
        assertThrows(ReglaDominioException.class, () -> new Porcentaje(101));
        assertThrows(ReglaDominioException.class, () -> new Porcentaje(-1));
        assertEquals(Dinero.pesos(30_000), new Porcentaje(30).aplicarA(Dinero.pesos(100_000)));
        assertEquals(new Porcentaje(70), new Porcentaje(30).complemento());
    }
}
