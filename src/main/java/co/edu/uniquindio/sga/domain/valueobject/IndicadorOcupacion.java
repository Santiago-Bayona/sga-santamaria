package co.edu.uniquindio.sga.domain.valueobject;

import java.math.BigDecimal;

/** Resultado del reporte de ocupación (7.9). porcentaje = nochesVendidas / (apartamentosActivos × nochesPeriodo) × 100. */
public record IndicadorOcupacion(PeriodoReporte periodo, long nochesVendidas, int apartamentosActivos,
                                 long nochesPeriodo, BigDecimal porcentaje) {
}
