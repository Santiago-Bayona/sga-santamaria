package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.LiquidacionPagoService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Pago;
import co.edu.uniquindio.sga.domain.valueobject.ResultadoMovimientoFolio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class RevertirPagoUseCase {

    private final LiquidacionPagoService liquidacionPagoService;

    public RevertirPagoUseCase(LiquidacionPagoService liquidacionPagoService) {
        this.liquidacionPagoService = liquidacionPagoService;
    }

    @Transactional
    public ResultadoMovimientoFolio<Pago> ejecutar(CodigoReserva codigoReserva, int numeroPago, LocalDate fecha) {

        return liquidacionPagoService.revertirPago(codigoReserva, numeroPago, fecha);
    }
}