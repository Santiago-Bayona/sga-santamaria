package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.TemporadaNoEncontradaException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.service.GestionTemporadasService;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetirarTemporadaUseCase {

    private final GestionTemporadasService gestionTemporadasService;
    private final AlojamientoRepository alojamientoRepository;

    public RetirarTemporadaUseCase(GestionTemporadasService gestionTemporadasService,
                                   AlojamientoRepository alojamientoRepository) {
        this.gestionTemporadasService = gestionTemporadasService;
        this.alojamientoRepository = alojamientoRepository;
    }

    @Transactional
    public void ejecutar(IdTemporada idTemporada) {

        boolean existe = alojamientoRepository.obtener().getTemporadas().stream()
                .anyMatch(temporada -> temporada.getId().equals(idTemporada));
        if (!existe) {
            throw new TemporadaNoEncontradaException(idTemporada);
        }

        gestionTemporadasService.retirarTemporada(idTemporada);
    }
}