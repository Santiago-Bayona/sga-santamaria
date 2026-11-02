package co.edu.uniquindio.sga.domain.valueobject;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Resultado del reporte de ingresos (7.9): pagos registrados en el periodo, por medio de pago.
 * Un reverso de pago registrado en el periodo resta, así que un medio puede quedar en negativo.
 */
public record IndicadorIngresos(PeriodoReporte periodo, Map<MedioPago, BigDecimal> porMedio, BigDecimal total) {

    public IndicadorIngresos {
        porMedio = Map.copyOf(porMedio);
    }
}
