package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.CotizadorEstanciaService;
import co.edu.uniquindio.sga.domain.valueobject.Cotizacion;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CotizarEstanciaUseCase {

    private final CotizadorEstanciaService cotizadorEstanciaService;

    public CotizarEstanciaUseCase(CotizadorEstanciaService cotizadorEstanciaService) {
        this.cotizadorEstanciaService = cotizadorEstanciaService;
    }

    @Transactional(readOnly = true)
    public Cotizacion ejecutar(IdentificacionApartamento identificacion, Estancia estancia,
                               List<Ocupante> ocupantes) {

        return cotizadorEstanciaService.cotizar(identificacion, estancia, ocupantes);
    }
}