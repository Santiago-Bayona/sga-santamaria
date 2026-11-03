package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.CotizarEstanciaRequest;
import co.edu.uniquindio.sga.application.dto.response.CotizacionResponse;
import co.edu.uniquindio.sga.application.usecase.CotizarEstanciaUseCase;
import co.edu.uniquindio.sga.infrastructure.web.mapper.RespuestaApiMapper;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * POST /api/cotizaciones, publico. Sigue en POST porque la estancia y los ocupantes no caben en la query.
 */

@RestController
@Validated
@RequestMapping("/api/cotizaciones")
public class CotizacionRestController {

    private final CotizarEstanciaUseCase cotizar;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;

    public CotizacionRestController(CotizarEstanciaUseCase cotizar, RespuestaApiMapper respuestas,
                                    SolicitudApiMapper solicitudes) {
        this.cotizar = cotizar;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
    }

    @PostMapping
    public CotizacionResponse cotizar(@Valid @RequestBody CotizarEstanciaRequest request) {
        return respuestas.cotizacion(cotizar.ejecutar(
                solicitudes.identificacion(request.identificacionApartamento()),
                solicitudes.estancia(request.fechaEntrada(), request.fechaSalida()),
                solicitudes.ocupantes(request.ocupantes())));
    }
}
