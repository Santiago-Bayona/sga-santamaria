package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;

/** DTO de salida de la API. Reservas de un canal de origen; el total incluye las canceladas y las no-show. */

public record ReservasPorCanalResponse(
        CanalOrigen canal,
        long total,
        long canceladas,
        long noShow
) {
}
