package co.edu.uniquindio.sga.infrastructure.observability;

import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health check de negocio mínimo: alojamiento singleton y política vigente.
 * Spring Boot 4 usa org.springframework.boot.health.contributor.
 */
@Component
public class SgaHealthIndicator implements HealthIndicator {

    private final AlojamientoRepository alojamientoRepository;
    private final PoliticaCancelacionRepository politicaRepository;

    public SgaHealthIndicator(AlojamientoRepository alojamientoRepository,
                              PoliticaCancelacionRepository politicaRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.politicaRepository = politicaRepository;
    }

    @Override
    public Health health() {
        try {
            var alojamiento = alojamientoRepository.obtener();
            var politica = politicaRepository.obtenerVigente();
            return Health.up()
                    .withDetail("alojamiento", alojamiento.getCodigo().valor())
                    .withDetail("politicaVersion", politica.getVersion().numero())
                    .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
