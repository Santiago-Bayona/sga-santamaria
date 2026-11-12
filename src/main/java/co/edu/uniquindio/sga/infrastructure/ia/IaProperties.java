package co.edu.uniquindio.sga.infrastructure.ia;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración del modelo de IA (10.2). Todo vive en application.yml: apagar o cambiar la IA no recompila.
 *
 * @param habilitada     interruptor general; apagado, el sistema opera y el resumen sale sin IA
 * @param proveedor      {@code simulado} (local, sin red) o {@code openai-compatible} (cualquier proveedor con la
 *                       API de chat de OpenAI: Groq, Gemini, OpenRouter, Ollama…)
 * @param urlBase        solo para openai-compatible: por ejemplo {@code https://api.groq.com/openai/v1}
 * @param modelo         solo para openai-compatible: nombre del modelo en el proveedor
 * @param tiempoEsperaMs tiempo máximo de espera de la respuesta del modelo (I-03)
 * @param clave          clave del proveedor (I-06): va en configuración externa, nunca versionada
 */
@ConfigurationProperties(prefix = "sga.ia")
public record IaProperties(
        boolean habilitada,
        String proveedor,
        String urlBase,
        String modelo,
        int tiempoEsperaMs,
        String clave
) {

    public static final String PROVEEDOR_SIMULADO = "simulado";
    public static final String PROVEEDOR_OPENAI_COMPATIBLE = "openai-compatible";
    public static final int TIEMPO_ESPERA_POR_DEFECTO_MS = 5000;

    public IaProperties {
        proveedor = (proveedor == null || proveedor.isBlank()) ? PROVEEDOR_SIMULADO : proveedor.trim();
        tiempoEsperaMs = tiempoEsperaMs <= 0 ? TIEMPO_ESPERA_POR_DEFECTO_MS : tiempoEsperaMs;
    }
}
