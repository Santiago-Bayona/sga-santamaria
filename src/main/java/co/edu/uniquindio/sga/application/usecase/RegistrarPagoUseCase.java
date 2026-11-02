package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.LiquidacionPagoService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.MedioPago;
import co.edu.uniquindio.sga.domain.valueobject.Pago;
import co.edu.uniquindio.sga.domain.valueobject.ResultadoMovimientoFolio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class RegistrarPagoUseCase {

    private final LiquidacionPagoService liquidacionPagoService;

    public RegistrarPagoUseCase(LiquidacionPagoService liquidacionPagoService) {
        this.liquidacionPagoService = liquidacionPagoService;
    }

    @Transactional
    public ResultadoMovimientoFolio<Pago> ejecutar(CodigoReserva codigoReserva, Dinero monto, MedioPago medioPago,
                                                   LocalDate fecha) {

        return liquidacionPagoService.registrarPago(codigoReserva, monto, medioPago, fecha);
    }
}