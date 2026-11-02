package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.RetiroApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetirarApartamentoUseCase {

    private final RetiroApartamentoService retiroApartamentoService;

    public RetirarApartamentoUseCase(RetiroApartamentoService retiroApartamentoService) {
        this.retiroApartamentoService = retiroApartamentoService;
    }

    @Transactional
    public void ejecutar(IdentificacionApartamento identificacion) {

        retiroApartamentoService.retirarDeVenta(identificacion);
    }
}