package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.GestionTemporadasService;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DefinirTarifaUseCase {

    private final GestionTemporadasService gestionTemporadasService;

    public DefinirTarifaUseCase(GestionTemporadasService gestionTemporadasService) {
        this.gestionTemporadasService = gestionTemporadasService;
    }

    @Transactional
    public void ejecutar(IdentificacionApartamento identificacion, IdTemporada idTemporada, Dinero valor, LocalDateTime momentoActual) {

        gestionTemporadasService.definirTarifa(identificacion, idTemporada, valor, momentoActual);
    }
}