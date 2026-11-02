package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.Saldo;

/**
 * Resultado de aplicar la política congelada (cancelación o no-show, RN-13).
 * Un saldo negativo es la devolución registrada a favor del huésped; el desembolso ocurre fuera del sistema (3.4).
 */
public record LiquidacionPolitica(CodigoReserva reserva, Dinero retencion, Saldo saldoFolio) {
}
