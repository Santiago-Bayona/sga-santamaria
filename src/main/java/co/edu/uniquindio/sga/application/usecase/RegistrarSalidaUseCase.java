package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.LiquidacionSalidaService;
import co.edu.uniquindio.sga.domain.valueobject.AutorizacionCierre;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrarSalidaUseCase {

    private final LiquidacionSalidaService liquidacionSalidaService;

    public RegistrarSalidaUseCase(LiquidacionSalidaService liquidacionSalidaService) {
        this.liquidacionSalidaService = liquidacionSalidaService;
    }

    /**
     * @param autorizacion autorización de salida con saldo pendiente (autor y motivo); {@code null} si no hay
     */
    @Transactional
    public void ejecutar(CodigoReserva codigoReserva, AutorizacionCierre autorizacion, LocalDateTime momentoActual) {

        liquidacionSalidaService.registrarSalida(codigoReserva, momentoActual, autorizacion);
    }
}