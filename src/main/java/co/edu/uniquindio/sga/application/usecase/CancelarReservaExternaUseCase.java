package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService;
import co.edu.uniquindio.sga.domain.service.LiquidacionPolitica;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CancelarReservaExternaUseCase {

    private final ConciliacionCanalExternoService conciliacionService;


    public CancelarReservaExternaUseCase(ConciliacionCanalExternoService conciliacionService) {
        this.conciliacionService = conciliacionService;
    }

    @Transactional
    public Optional<LiquidacionPolitica> ejecutar(CodigoCanalExterno codigoCanal,
                                                  IdentificadorExterno identificador,
                            LocalDateTime momentoActual) {

        return conciliacionService.cancelarReservaExterna(codigoCanal, identificador, momentoActual);
    }
}