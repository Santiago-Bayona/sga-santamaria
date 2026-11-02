package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.LiquidacionPagoService;
import co.edu.uniquindio.sga.domain.valueobject.Cargo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.ResultadoMovimientoFolio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class RevertirCargoUseCase {

    private final LiquidacionPagoService liquidacionPagoService;

    public RevertirCargoUseCase(LiquidacionPagoService liquidacionPagoService) {
        this.liquidacionPagoService = liquidacionPagoService;
    }

    @Transactional
    public ResultadoMovimientoFolio<Cargo> ejecutar(CodigoReserva codigoReserva, int numeroCargo, LocalDate fecha) {

        return liquidacionPagoService.revertirCargo(codigoReserva, numeroCargo, fecha);
    }
}