package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Concepto del folio: alojamiento, servicio, ajuste o penalidad (7.7). Inmutable (RN-16).
 * Tiene identidad local dentro del Folio (numero). El signo es explícito (resta) y es independiente de ser
 * un reverso: un ajuste negativo resta pero no anula nada; un reverso apunta al numero exacto que anula.
 */
public record Cargo(int numero, TipoCargo tipo, String concepto, Dinero monto, LocalDate fecha,
                    boolean resta, Integer revierteA) {

    public Cargo {
        if (numero < 1) {
            throw new ReglaDominioException("El número del cargo dentro del folio debe ser positivo.");
        }
        if (tipo == null || fecha == null || monto == null) {
            throw new ReglaDominioException("El cargo requiere tipo, monto y fecha.");
        }
        if (concepto == null || concepto.isBlank()) {
            throw new ReglaDominioException("El cargo requiere un concepto.");
        }
        if (monto.esCero()) {
            throw new ReglaDominioException("El monto del cargo debe ser mayor que cero.");
        }
        if (revierteA != null && (revierteA < 1 || revierteA >= numero)) {
            throw new ReglaDominioException("Un reverso solo puede anular un cargo registrado antes que él.");
        }
        concepto = concepto.trim();
    }

    /** Cargo que suma al saldo. */
    public static Cargo suma(int numero, TipoCargo tipo, String concepto, Dinero monto, LocalDate fecha) {
        return new Cargo(numero, tipo, concepto, monto, fecha, false, null);
    }

    /** Cargo que resta al saldo sin anular otro (p. ej. ajuste negativo por modificación, RN-14). */
    public static Cargo descuento(int numero, TipoCargo tipo, String concepto, Dinero monto, LocalDate fecha) {
        return new Cargo(numero, tipo, concepto, monto, fecha, true, null);
    }

    /** RN-16: anula exactamente el cargo original con el signo contrario. */
    public static Cargo reverso(int numero, Cargo original, LocalDate fechaCorreccion) {
        if (original == null || original.esReverso()) {
            throw new ReglaDominioException("Solo se puede revertir un cargo original, no otro reverso.");
        }
        if (fechaCorreccion == null || fechaCorreccion.isBefore(original.fecha)) {
            throw new ReglaDominioException("El reverso no puede tener fecha anterior al cargo que anula.");
        }
        return new Cargo(numero, original.tipo, "Reverso del cargo #" + original.numero + ": " + original.concepto,
                original.monto, fechaCorreccion, !original.resta, original.numero);
    }

    public boolean esReverso() {
        return revierteA != null;
    }

    public boolean esReversoDe(Cargo otro) {
        return esReverso() && revierteA == otro.numero;
    }

    /** Positivo si suma al saldo, negativo si resta. */
    public BigDecimal valorConSigno() {
        return resta ? monto.valor().negate() : monto.valor();
    }
}
