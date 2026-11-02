package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Los parámetros de la sección 6 son configuración. Un cambio aquí NO altera las reservas ya
 * creadas (RN-22): ellas conservan su valor y su política congelados.
 */

@Service
public class ActualizarConfiguracionAlojamientoUseCase {

    private final AlojamientoRepository alojamientoRepository;

    public ActualizarConfiguracionAlojamientoUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    /**
     * Recibe la configuración ya construida (los tiempos llegan en minutos desde el mapper de la API, así que
     * 2 h 30 min es representable). El dominio valida la coherencia, por ejemplo la hora límite de no-show.
     */
    @Transactional
    public Alojamiento ejecutar(ConfiguracionAlojamiento nueva) {

        Alojamiento alojamiento = alojamientoRepository.obtener();
        alojamiento.actualizarConfiguracion(nueva);
        alojamientoRepository.guardar(alojamiento);
        return alojamiento;
    }
}
