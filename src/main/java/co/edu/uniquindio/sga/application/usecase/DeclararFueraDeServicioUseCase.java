package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.service.PreparacionApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeclararFueraDeServicioUseCase {

    private final PreparacionApartamentoService preparacionApartamentoService;

    public DeclararFueraDeServicioUseCase(PreparacionApartamentoService preparacionApartamentoService) {
        this.preparacionApartamentoService = preparacionApartamentoService;
    }

    @Transactional
    public List<Reserva> ejecutar(IdentificacionApartamento identificacion) {

        return preparacionApartamentoService.declararFueraDeServicio(identificacion);
    }
}