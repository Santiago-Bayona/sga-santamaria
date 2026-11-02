package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.PreparacionApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReintegrarAlServicioUseCase {

    private final PreparacionApartamentoService preparacionApartamentoService;

    public ReintegrarAlServicioUseCase(PreparacionApartamentoService preparacionApartamentoService) {
        this.preparacionApartamentoService = preparacionApartamentoService;
    }

    @Transactional
    public void ejecutar(IdentificacionApartamento identificacion) {

        preparacionApartamentoService.reintegrarAlServicio(identificacion);
    }
}