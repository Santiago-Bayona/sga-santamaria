package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.DefinirPoliticaCancelacionRequest;
import co.edu.uniquindio.sga.application.dto.response.PoliticaCancelacionResponse;
import co.edu.uniquindio.sga.application.usecase.ConsultarPoliticaVigenteUseCase;
import co.edu.uniquindio.sga.application.usecase.DefinirPoliticaCancelacionUseCase;
import co.edu.uniquindio.sga.infrastructure.web.RespuestasHttp;
import co.edu.uniquindio.sga.infrastructure.web.mapper.RespuestaApiMapper;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Consulta la política vigente y publica una versión nueva. El alta responde 201 hacia /api/politicas/vigente.
 */

@RestController
@Validated
@RequestMapping("/api/politicas")
public class PoliticaCancelacionRestController {

    private final ConsultarPoliticaVigenteUseCase vigente;
    private final DefinirPoliticaCancelacionUseCase definir;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;
    private final Clock reloj;

    public PoliticaCancelacionRestController(ConsultarPoliticaVigenteUseCase vigente,
                                             DefinirPoliticaCancelacionUseCase definir,
                                             RespuestaApiMapper respuestas,
                                             SolicitudApiMapper solicitudes,
                                             Clock reloj) {
        this.vigente = vigente;
        this.definir = definir;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
        this.reloj = reloj;
    }

    @GetMapping("/vigente")
    public PoliticaCancelacionResponse vigente() {
        return respuestas.politica(vigente.ejecutar());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PoliticaCancelacionResponse> definir(@Valid @RequestBody DefinirPoliticaCancelacionRequest request) {
        var politica = definir.ejecutar(
                solicitudes.tramos(request.tramos()),
                solicitudes.porcentaje(request.retencionNoShow()),
                LocalDateTime.now(reloj));
        return RespuestasHttp.creado("/api/politicas/vigente", respuestas.politica(politica));
    }
}
