package co.edu.uniquindio.sga.infrastructure.config;

import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.PlazoConfirmacion;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.TiempoPreparacion;
import co.edu.uniquindio.sga.domain.valueobject.UmbralEdadFacturable;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Parámetros de la ficha del alojamiento (check-in, check-out, preparación, confirmación, edad y recordatorios).
 */
@ConfigurationProperties(prefix = "sga.ficha")
public record FichaProperties(
        LocalTime horaEntrada,
        LocalTime horaSalida,
        int tiempoPreparacionHoras,
        int plazoConfirmacionHoras,
        LocalTime horaLimiteNoShow,
        int umbralEdadFacturable,
        int porcentajeAnticipo,
        int antelacionRecordatorioEntradaHoras,
        int antelacionRecordatorioSalidaHoras
) {

    public ConfiguracionAlojamiento configuracion() {
        return new ConfiguracionAlojamiento(
                horaEntrada,
                horaSalida,
                new TiempoPreparacion(Duration.ofHours(tiempoPreparacionHoras)),
                new PlazoConfirmacion(plazoConfirmacionHoras),
                horaLimiteNoShow,
                new UmbralEdadFacturable(umbralEdadFacturable),
                new Porcentaje(porcentajeAnticipo),
                Duration.ofHours(antelacionRecordatorioEntradaHoras),
                Duration.ofHours(antelacionRecordatorioSalidaHoras));
    }
}
