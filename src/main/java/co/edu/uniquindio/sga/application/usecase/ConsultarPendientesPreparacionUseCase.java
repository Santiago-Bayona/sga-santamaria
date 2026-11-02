package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.service.PreparacionApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarPendientesPreparacionUseCase {

    private final PreparacionApartamentoService preparacionApartamentoService;

    public ConsultarPendientesPreparacionUseCase(PreparacionApartamentoService preparacionApartamentoService) {
        this.preparacionApartamentoService = preparacionApartamentoService;
    }

    @Transactional(readOnly = true)
    public Pagina<Apartamento> ejecutar(int pagina) {

        return PaginacionEnMemoria.de(preparacionApartamentoService.pendientesDePreparacion(), pagina);
    }
}