package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.valueobject.ServicioAdicional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgregarServicioAdicionalAlojamientoUseCase {

    private final AlojamientoRepository alojamientoRepository;

    public AgregarServicioAdicionalAlojamientoUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    @Transactional
    public Alojamiento ejecutar(ServicioAdicional servicio) {

        Alojamiento alojamiento = alojamientoRepository.obtener();

        // El dominio rechaza nombres repetidos y un servicio con cargo sin valor.
        alojamiento.agregarServicioAdicional(servicio);

        alojamientoRepository.guardar(alojamiento);
        return alojamiento;
    }
}
