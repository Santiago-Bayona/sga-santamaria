package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.EntregaApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrarLlegadaUseCase {

    private final EntregaApartamentoService entregaApartamentoService;

    public RegistrarLlegadaUseCase(EntregaApartamentoService entregaApartamentoService) {
        this.entregaApartamentoService = entregaApartamentoService;
    }

    @Transactional
    public void ejecutar(CodigoReserva codigoReserva, LocalDateTime momentoActual) {

        entregaApartamentoService.registrarLlegada(codigoReserva, momentoActual);
    }
}
