package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.service.GestionTemporadasService;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CrearTemporadaUseCase {

    private final GestionTemporadasService gestionTemporadasService;

    public CrearTemporadaUseCase(GestionTemporadasService gestionTemporadasService) {
        this.gestionTemporadasService = gestionTemporadasService;
    }

    /**
     * @param temporada la temporada ya construida (base o especial, con sus periodos)
     * @param tarifas   tarifa por apartamento para la nueva temporada
     */
    @Transactional
    public void ejecutar(Temporada temporada, Map<IdentificacionApartamento, Dinero> tarifas, LocalDateTime momentoActual) {

        gestionTemporadasService.agregarTemporada(temporada, tarifas, momentoActual);
    }

}