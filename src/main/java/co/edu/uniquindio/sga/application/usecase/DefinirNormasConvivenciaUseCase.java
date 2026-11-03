package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefinirNormasConvivenciaUseCase {

    private final AlojamientoRepository alojamientoRepository;

    public DefinirNormasConvivenciaUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    @Transactional
    public Alojamiento ejecutar(List<String> normas) {

        Alojamiento alojamiento = alojamientoRepository.obtener();
        alojamiento.definirNormasConvivencia(normas);

        alojamientoRepository.guardar(alojamiento);
        return alojamiento;
    }
}
