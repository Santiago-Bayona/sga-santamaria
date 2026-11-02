package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.MedioPago;

import java.time.LocalDate;

/** DTO de salida de la API. Un pago del folio con su medio y fecha (RN-15). Una corrección es otro pago con esReverso = true (RN-16). */
public record PagoResponse(

        int numero,
        long monto,
        MedioPago medio,
        LocalDate fecha,
        boolean esReverso,
        Integer revierteA
) {
}
