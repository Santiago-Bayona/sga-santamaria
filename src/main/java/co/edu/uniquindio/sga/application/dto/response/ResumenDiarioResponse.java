package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;
import co.edu.uniquindio.sga.domain.valueobject.ResumenDiario;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de salida (Guía 07): resumen diario de llegadas y salidas. Siempre trae las listas y un texto; si
 * {@code generadoPorIa} es falso, {@code motivoDegradacion} explica por qué.
 */
public record ResumenDiarioResponse(
        LocalDate fecha,
        int totalLlegadas,
        int totalSalidas,
        List<MovimientoDiarioResponse> llegadas,
        List<MovimientoDiarioResponse> salidas,
        String texto,
        boolean generadoPorIa,
        MotivoDegradacionIa motivoDegradacion       // nulo cuando lo redactó la IA
) {

    public static ResumenDiarioResponse de(ResumenDiario resumen) {
        var datos = resumen.datos();
        return new ResumenDiarioResponse(datos.fecha(), datos.totalLlegadas(), datos.totalSalidas(),
                datos.llegadas().stream().map(MovimientoDiarioResponse::de).toList(),
                datos.salidas().stream().map(MovimientoDiarioResponse::de).toList(),
                resumen.texto(), resumen.generadoPorIa(), resumen.motivoDegradacion());
    }
}
