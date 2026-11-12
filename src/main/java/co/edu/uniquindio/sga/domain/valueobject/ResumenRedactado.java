package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

/**
 * Texto que devuelve el generador de resumen.
 *
 * @param texto            resumen redactado, no vacío
 * @param generadoPorModelo verdadero solo si lo redactó un modelo de IA real; la implementación local simulada
 *                          devuelve falso
 */
public record ResumenRedactado(String texto, boolean generadoPorModelo) {

    public ResumenRedactado {
        if (texto == null || texto.isBlank()) {
            throw new ReglaDominioException("El resumen redactado no puede estar vacío.");
        }
    }
}
