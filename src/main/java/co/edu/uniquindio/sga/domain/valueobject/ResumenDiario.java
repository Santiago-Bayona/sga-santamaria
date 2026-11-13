package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

/**
 * Resumen diario de llegadas y salidas. Siempre trae las listas y un texto: si la IA no pudo redactarlo, el texto
 * es el resumen básico y {@code motivoDegradacion} dice por qué (10.2: se degrada de forma visible).
 */
public record ResumenDiario(
        DatosResumenDiario datos,
        String texto,
        boolean generadoPorIa,
        MotivoDegradacionIa motivoDegradacion
) {

    public ResumenDiario {
        if (datos == null) {
            throw new ReglaDominioException("El resumen diario requiere sus datos.");
        }
        if (texto == null || texto.isBlank()) {
            throw new ReglaDominioException("El resumen diario requiere un texto.");
        }
        if (generadoPorIa && motivoDegradacion != null) {
            throw new ReglaDominioException("Un resumen generado por IA no tiene motivo de degradación.");
        }
        if (!generadoPorIa && motivoDegradacion == null) {
            throw new ReglaDominioException("Un resumen sin IA debe indicar el motivo de degradación.");
        }
    }
}
