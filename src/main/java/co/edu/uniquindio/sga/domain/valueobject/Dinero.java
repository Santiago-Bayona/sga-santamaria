package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Valor monetario en pesos colombianos (3.4): moneda única, sin decimales, precisión exacta.
 * El constructor exige pesos enteros. El redondeo al peso más cercano se aplica UNA sola vez,
 * al final del cálculo de cada cargo, con {@link #redondeado(BigDecimal)}; nunca noche por noche.
 */
public record Dinero(BigDecimal valor) {

    public static final Dinero CERO = new Dinero(BigDecimal.ZERO);

    public Dinero {
        if (valor == null) {
            throw new ReglaDominioException("El valor monetario es obligatorio.");
        }
        if (valor.signum() < 0) {
            throw new ReglaDominioException("El valor monetario no puede ser negativo.");
        }
        if (valor.stripTrailingZeros().scale() > 0) {
            throw new ReglaDominioException("El valor en COP no admite decimales: redondee al final del cálculo.");
        }
        // Escala 0 normalizada: 1000 y 1000.00 son el mismo valor (igualdad del record)
        valor = valor.setScale(0, RoundingMode.UNNECESSARY);
    }

    public static Dinero pesos(long pesos) {
        return new Dinero(BigDecimal.valueOf(pesos));
    }

    /** 3.4: redondeo al peso más cercano, solo para el resultado final de un cálculo. */
    public static Dinero redondeado(BigDecimal calculo) {
        if (calculo == null) {
            throw new ReglaDominioException("El valor monetario es obligatorio.");
        }
        return new Dinero(calculo.setScale(0, RoundingMode.HALF_UP));
    }

    public Dinero sumar(Dinero otro) {
        return new Dinero(valor.add(otro.valor));
    }

    /** Lanza excepción si el resultado fuera negativo: para diferencias con signo use BigDecimal o Saldo. */
    public Dinero restar(Dinero otro) {
        return new Dinero(valor.subtract(otro.valor));
    }

    public Dinero multiplicarPor(int factor) {
        return new Dinero(valor.multiply(BigDecimal.valueOf(factor)));
    }

    public boolean esCero() {
        return valor.signum() == 0;
    }

    public boolean esMayorQue(Dinero otro) {
        return valor.compareTo(otro.valor) > 0;
    }
}