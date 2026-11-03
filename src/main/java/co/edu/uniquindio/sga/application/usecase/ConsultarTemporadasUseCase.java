package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultarTemporadasUseCase {

    private final AlojamientoRepository alojamientoRepository;

    public ConsultarTemporadasUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    @Transactional(readOnly = true)
    public List<Temporada> ejecutar() {
        return alojamientoRepository.obtener().getTemporadas();
    }
}
