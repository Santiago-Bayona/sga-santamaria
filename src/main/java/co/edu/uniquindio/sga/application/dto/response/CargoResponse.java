package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.TipoCargo;

import java.time.LocalDate;

/** DTO de salida de la API. Un cargo del folio. Los cargos no se editan (RN-16): una corrección es otro cargo con esReverso = true. */
public record CargoResponse(
        int numero,
        TipoCargo tipo,
        String concepto,
        long monto,
        LocalDate fecha,
        boolean esReverso,
        Integer revierteA
) {
}
