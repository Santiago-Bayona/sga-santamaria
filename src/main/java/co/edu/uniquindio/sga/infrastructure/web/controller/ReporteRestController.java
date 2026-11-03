package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.response.ReporteIngresosResponse;
import co.edu.uniquindio.sga.application.dto.response.ReporteOcupacionResponse;
import co.edu.uniquindio.sga.application.dto.response.ReporteReservasPorCanalResponse;
import co.edu.uniquindio.sga.application.usecase.GenerarReporteIngresosUseCase;
import co.edu.uniquindio.sga.application.usecase.GenerarReporteOcupacionUseCase;
import co.edu.uniquindio.sga.application.usecase.GenerarReporteReservasPorCanalUseCase;
import co.edu.uniquindio.sga.infrastructure.web.mapper.RespuestaApiMapper;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * GET /api/reportes/ingresos, /ocupacion y /canales. Solo ADMIN. El periodo viaja en query params.
 */

@RestController
@Validated
@RequestMapping("/api/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteRestController {

    private final GenerarReporteIngresosUseCase ingresos;
    private final GenerarReporteOcupacionUseCase ocupacion;
    private final GenerarReporteReservasPorCanalUseCase canales;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;

    public ReporteRestController(GenerarReporteIngresosUseCase ingresos, GenerarReporteOcupacionUseCase ocupacion,
                                 GenerarReporteReservasPorCanalUseCase canales, RespuestaApiMapper respuestas,
                                 SolicitudApiMapper solicitudes) {
        this.ingresos = ingresos;
        this.ocupacion = ocupacion;
        this.canales = canales;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
    }

    @GetMapping("/ingresos")
    public ReporteIngresosResponse ingresos(@RequestParam @NotNull LocalDate fechaDesde,
                                            @RequestParam @NotNull LocalDate fechaHasta) {
        return respuestas.ingresos(ingresos.ejecutar(solicitudes.periodo(fechaDesde, fechaHasta)));
    }

    @GetMapping("/ocupacion")
    public ReporteOcupacionResponse ocupacion(@RequestParam @NotNull LocalDate fechaDesde,
                                              @RequestParam @NotNull LocalDate fechaHasta) {
        return respuestas.ocupacion(ocupacion.ejecutar(solicitudes.periodo(fechaDesde, fechaHasta)));
    }

    @GetMapping("/canales")
    public ReporteReservasPorCanalResponse canales(@RequestParam @NotNull LocalDate fechaDesde,
                                                   @RequestParam @NotNull LocalDate fechaHasta) {
        var periodo = solicitudes.periodo(fechaDesde, fechaHasta);
        return respuestas.reservasPorCanal(periodo, canales.ejecutar(periodo));
    }
}
