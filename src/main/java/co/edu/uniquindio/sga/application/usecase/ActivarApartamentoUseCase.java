package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.GestionTemporadasService;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivarApartamentoUseCase {

    private final GestionTemporadasService gestionTemporadasService;

    public ActivarApartamentoUseCase(GestionTemporadasService gestionTemporadasService) {
        this.gestionTemporadasService = gestionTemporadasService;
    }

    @Transactional
    public void ejecutar(IdentificacionApartamento identificacion) {

        gestionTemporadasService.activarApartamento(identificacion);
    }
}