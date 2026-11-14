package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.response.CalendarioOcupacionResponse;
import co.edu.uniquindio.sga.application.usecase.ConsultarCalendarioOcupacionUseCase;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * GET /api/calendario. Ocupacion de un apartamento entre dos fechas, sin cuerpo JSON.
 */

@RestController
@Validated
@RequestMapping("/api/calendario")
public class CalendarioRestController {

    private final ConsultarCalendarioOcupacionUseCase calendario;
    private final SolicitudApiMapper solicitudes;

    public CalendarioRestController(ConsultarCalendarioOcupacionUseCase calendario, SolicitudApiMapper solicitudes) {
        this.calendario = calendario;
        this.solicitudes = solicitudes;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
    public CalendarioOcupacionResponse consultar(@RequestParam @NotBlank String identificacion,
                                                 @RequestParam @NotNull LocalDate fechaDesde,
                                                 @RequestParam @NotNull LocalDate fechaHasta) {
        return calendario.ejecutar(solicitudes.identificacion(identificacion), solicitudes.estancia(fechaDesde, fechaHasta));
    }
}
