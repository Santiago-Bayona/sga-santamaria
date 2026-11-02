package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.service.GestionBloqueoApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.MotivoBloqueo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrarBloqueoUseCase {

    private final GestionBloqueoApartamentoService gestionBloqueoApartamentoService;

    public RegistrarBloqueoUseCase(GestionBloqueoApartamentoService gestionBloqueoApartamentoService) {
        this.gestionBloqueoApartamentoService = gestionBloqueoApartamentoService;
    }

    @Transactional
    public Bloqueo ejecutar(CodigoBloqueo codigo, IdentificacionApartamento identificacion, Estancia rango,
                            MotivoBloqueo motivo,
                            LocalDateTime momentoActual) {

        return gestionBloqueoApartamentoService.registrarBloqueo(
                codigo,
                identificacion,
                rango,
                motivo,
                momentoActual
        );
    }
}