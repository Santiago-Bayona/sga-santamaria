package co.edu.uniquindio.sga.infrastructure.ia;

import co.edu.uniquindio.sga.domain.exception.GeneradorResumenNoDisponibleException;
import co.edu.uniquindio.sga.domain.port.GeneradorResumenPort;
import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Elige el adaptador del puerto de IA según la configuración (10.2): apagarla o cambiar de proveedor es cambiar
 * {@code sga.ia.*}, sin tocar código. Por defecto la IA queda apagada.
 */
@Configuration
@EnableConfigurationProperties(IaProperties.class)
public class IaConfig {

    @Bean
    public GeneradorResumenPort generadorResumen(IaProperties ia) {
        return crear(ia);
    }

    static GeneradorResumenPort crear(IaProperties ia) {
        if (!ia.habilitada()) {
            return new ResumenDeshabilitadoAdapter();
        }
        return switch (ia.proveedor()) {
            case IaProperties.PROVEEDOR_SIMULADO -> new ResumenSimuladoAdapter();
            case IaProperties.PROVEEDOR_OPENAI_COMPATIBLE -> new ResumenLlmAdapter(ia.urlBase(), ia.modelo(),
                    ia.clave(), Duration.ofMillis(ia.tiempoEsperaMs()));
            // Un valor mal escrito no debe tumbar el arranque (I-05): el resumen sale sin IA y queda registrado.
            default -> datos -> {
                throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.ERROR_PROVEEDOR,
                        "Proveedor de IA desconocido en sga.ia.proveedor: " + ia.proveedor());
            };
        };
    }
}
