package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Abono registrado contra un folio, con medio y fecha (RN-15). Inmutable (RN-16).
 * Tiene identidad local dentro del Folio: el numero lo asigna el Folio y solo tiene sentido dentro de él.
 * Un reverso apunta al numero exacto del pago que anula (revierteA), así dos pagos iguales nunca se confunden.
 */
public record Pago(int numero, Dinero monto, MedioPago medio, LocalDate fecha, Integer revierteA) {

    public Pago {
        if (numero < 1) {
            throw new ReglaDominioException("El número del pago dentro del folio debe ser positivo.");
        }
        if (monto == null || medio == null || fecha == null) {
            throw new ReglaDominioException("Todo pago requiere monto, medio y fecha.");
        }
        if (monto.esCero()) {
            throw new ReglaDominioException("El monto del pago debe ser mayor que cero.");
        }
        if (revierteA != null && (revierteA < 1 || revierteA >= numero)) {
            throw new ReglaDominioException("Un reverso solo puede anular un pago registrado antes que él.");
        }
    }

    public static Pago original(int numero, Dinero monto, MedioPago medio, LocalDate fecha) {
        return new Pago(numero, monto, medio, fecha, null);
    }

    /** RN-16: mismo monto y medio que el original, con la fecha de la corrección. */
    public static Pago reverso(int numero, Pago original, LocalDate fechaCorreccion) {
        if (original == null || original.esReverso()) {
            throw new ReglaDominioException("Solo se puede revertir un pago original, no otro reverso.");
        }
        if (fechaCorreccion == null || fechaCorreccion.isBefore(original.fecha)) {
            throw new ReglaDominioException("El reverso no puede tener fecha anterior al pago que anula.");
        }
        return new Pago(numero, original.monto, original.medio, fechaCorreccion, original.numero);
    }

    public boolean esReverso() {
        return revierteA != null;
    }

    public boolean esReversoDe(Pago otro) {
        return esReverso() && revierteA == otro.numero;
    }

    /** Positivo si abona, negativo si es un reverso. */
    public BigDecimal valorConSigno() {
        return esReverso() ? monto.valor().negate() : monto.valor();
    }
}