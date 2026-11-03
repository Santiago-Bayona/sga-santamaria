package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.valueobject.Ubicacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarDatosAlojamientoUseCase {

    private final AlojamientoRepository alojamientoRepository;

    public ActualizarDatosAlojamientoUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    @Transactional
    public Alojamiento ejecutar(String nombre, String descripcion, Ubicacion ubicacion) {

        Alojamiento alojamiento = alojamientoRepository.obtener();
        alojamiento.actualizarDescripcion(nombre, descripcion);
        alojamiento.cambiarUbicacion(ubicacion);

        alojamientoRepository.guardar(alojamiento);
        return alojamiento;
    }
}
