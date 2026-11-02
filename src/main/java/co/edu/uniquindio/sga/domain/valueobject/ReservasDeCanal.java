package co.edu.uniquindio.sga.domain.valueobject;

/** Una fila del reporte de reservas por canal (7.9): total, y cuántas terminaron canceladas o en no-show. */
public record ReservasDeCanal(CanalOrigen canal, long total, long canceladas, long noShow) {
}
