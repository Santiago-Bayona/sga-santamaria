package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.LiquidacionPagoService;
import co.edu.uniquindio.sga.domain.valueobject.AutorizacionCierre;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CerrarFolioUseCase {

    private final LiquidacionPagoService liquidacionPagoService;

    public CerrarFolioUseCase(LiquidacionPagoService liquidacionPagoService) {
        this.liquidacionPagoService = liquidacionPagoService;
    }

    /**
     * @param autorizacion autorización de cierre con saldo pendiente (autor y motivo); {@code null} si el cierre
     *                     no se autoriza con deuda. Quién autoriza lo resuelve infraestructura desde la sesión.
     */
    @Transactional
    public void ejecutar(CodigoReserva codigoReserva, AutorizacionCierre autorizacion) {

        liquidacionPagoService.cerrarFolio(codigoReserva, autorizacion);
    }
}





