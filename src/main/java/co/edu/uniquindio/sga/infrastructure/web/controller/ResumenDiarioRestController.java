package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.response.ResumenDiarioResponse;
import co.edu.uniquindio.sga.application.usecase.GenerarResumenDiarioUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Resumen diario de llegadas y salidas previstas (ficha A.6, 10.2). Responde siempre: si la IA está apagada o falla,
 * el resumen sale con el texto básico, {@code generadoPorIa: false} y el motivo. Sin fecha, usa el día de hoy.
 */
@RestController
@RequestMapping("/api/operacion")
@PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
public class ResumenDiarioRestController {

    private final GenerarResumenDiarioUseCase resumenDiario;
    private final Clock reloj;

    public ResumenDiarioRestController(GenerarResumenDiarioUseCase resumenDiario, Clock reloj) {
        this.resumenDiario = resumenDiario;
        this.reloj = reloj;
    }

    @GetMapping("/resumen-diario")
    public ResumenDiarioResponse resumenDiario(
            @RequestParam(required = false) LocalDate fecha) {
        LocalDate dia = fecha != null ? fecha : LocalDate.now(reloj);
        return ResumenDiarioResponse.de(resumenDiario.ejecutar(dia));
    }
}
