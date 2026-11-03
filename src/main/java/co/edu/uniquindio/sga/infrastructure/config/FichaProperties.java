package co.edu.uniquindio.sga.infrastructure.config;

import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.PlazoConfirmacion;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.TiempoPreparacion;
import co.edu.uniquindio.sga.domain.valueobject.UmbralEdadFacturable;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Ficha del Alojamiento (Anexo A) completa: parámetros (A.4), inventario (A.2), temporadas y tarifas (A.3),
 * política de cancelación (A.4) y servicios (A.5). Todo vive en application.yml: cambiar un valor no recompila.
 */
@ConfigurationProperties(prefix = "sga.ficha")
public record FichaProperties(
        LocalTime horaEntrada,
        LocalTime horaSalida,
        int tiempoPreparacionMinutos,
        int plazoConfirmacionHoras,
        LocalTime horaLimiteNoShow,
        int umbralEdadFacturable,
        int porcentajeAnticipo,
        int antelacionRecordatorioEntradaHoras,
        int antelacionRecordatorioSalidaHoras,
        Identidad identidad,
        List<String> normas,
        List<Servicio> servicios,
        List<TemporadaFicha> temporadas,
        Politica politica,
        List<ApartamentoFicha> apartamentos
) {

    public ConfiguracionAlojamiento configuracion() {
        return new ConfiguracionAlojamiento(
                horaEntrada,
                horaSalida,
                new TiempoPreparacion(Duration.ofMinutes(tiempoPreparacionMinutos)),
                new PlazoConfirmacion(plazoConfirmacionHoras),
                horaLimiteNoShow,
                new UmbralEdadFacturable(umbralEdadFacturable),
                new Porcentaje(porcentajeAnticipo),
                Duration.ofHours(antelacionRecordatorioEntradaHoras),
                Duration.ofHours(antelacionRecordatorioSalidaHoras));
    }

    /** A.1 */
    public record Identidad(String codigo, String nombre, String descripcion, String ciudad, String direccion,
                            BigDecimal latitud, BigDecimal longitud) {
    }

    /** A.5: servicio adicional con su cargo. */
    public record Servicio(String nombre, long valor, boolean generaCargo) {
    }

    /**
     * A.3: temporada con sus periodos en formato "MM-dd/MM-dd" (ej. "12-01/01-11").
     * La temporada base va con {@code base: true} y sin periodos.
     */
    public record TemporadaFicha(String id, String nombre, boolean base, List<String> periodos) {
        public TemporadaFicha {
            periodos = periodos == null ? List.of() : periodos;
        }
    }

    /** A.4: tramos (días de antelación mínima → % de retención) y consecuencia del no-show. */
    public record Politica(String codigo, List<Tramo> tramos, int retencionNoShow) {
    }

    public record Tramo(int diasAntelacionMinima, int retencion) {
    }

    /** A.2 + A.3: apartamento con sus tarifas por temporada (id de temporada → valor por ocupante facturable). */
    public record ApartamentoFicha(String id, String nombre, String descripcion, int dormitorios, int capacidad,
                                   List<String> dotacion, Map<String, Long> tarifas) {
    }
}
