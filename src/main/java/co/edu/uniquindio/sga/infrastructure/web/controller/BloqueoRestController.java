package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.RegistrarBloqueoRequest;
import co.edu.uniquindio.sga.application.dto.request.ReprogramarBloqueoRequest;
import co.edu.uniquindio.sga.application.dto.response.BloqueoResponse;
import co.edu.uniquindio.sga.application.dto.response.PaginaResponse;
import co.edu.uniquindio.sga.application.usecase.ConsultarBloqueosUseCase;
import co.edu.uniquindio.sga.application.usecase.LevantarBloqueoUseCase;
import co.edu.uniquindio.sga.application.usecase.RegistrarBloqueoUseCase;
import co.edu.uniquindio.sga.application.usecase.ReprogramarBloqueoUseCase;
import co.edu.uniquindio.sga.infrastructure.web.RespuestasHttp;
import co.edu.uniquindio.sga.infrastructure.web.mapper.RespuestaApiMapper;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Crea, consulta, reprograma y levanta bloqueos. La consulta es GET con la identificación del apartamento.
 */

@RestController
@Validated
@RequestMapping("/api/bloqueos")
public class BloqueoRestController {

    private final RegistrarBloqueoUseCase registrar;
    private final ConsultarBloqueosUseCase consultar;
    private final ReprogramarBloqueoUseCase reprogramar;
    private final LevantarBloqueoUseCase levantar;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;
    private final Clock reloj;

    public BloqueoRestController(RegistrarBloqueoUseCase registrar, ConsultarBloqueosUseCase consultar,
                                 ReprogramarBloqueoUseCase reprogramar, LevantarBloqueoUseCase levantar,
                                 RespuestaApiMapper respuestas, SolicitudApiMapper solicitudes, Clock reloj) {
        this.registrar = registrar;
        this.consultar = consultar;
        this.reprogramar = reprogramar;
        this.levantar = levantar;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
        this.reloj = reloj;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
    public ResponseEntity<BloqueoResponse> registrar(@Valid @RequestBody RegistrarBloqueoRequest request) {
        var bloqueo = registrar.ejecutar(
                solicitudes.codigoBloqueo(request.codigoBloqueo()),
                solicitudes.identificacion(request.identificacionApartamento()),
                solicitudes.estancia(request.fechaInicio(), request.fechaFin()),
                solicitudes.motivoBloqueo(request.motivo()),
                LocalDateTime.now(reloj));
        return RespuestasHttp.creado("/api/bloqueos/" + bloqueo.getCodigo().valor(), respuestas.bloqueo(bloqueo));
    }

    @GetMapping
    public PaginaResponse<BloqueoResponse> consultar(@RequestParam @NotBlank String identificacion,
                                                     @RequestParam(defaultValue = "0") int pagina) {
        var resultado = consultar.ejecutar(solicitudes.identificacion(identificacion), pagina);
        return PaginaResponse.desde(resultado.contenido().stream().map(respuestas::bloqueo).toList(),
                resultado.numero(), resultado.total());
    }

    @PostMapping("/{codigo}/reprogramacion")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
    public ResponseEntity<Void> reprogramar(@PathVariable String codigo,
                                            @Valid @RequestBody ReprogramarBloqueoRequest request) {
        reprogramar.ejecutar(solicitudes.codigoBloqueo(codigo),
                solicitudes.estancia(request.nuevaFechaInicio(), request.nuevaFechaFin()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{codigo}/levantamiento")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
    public ResponseEntity<Void> levantar(@PathVariable String codigo) {
        levantar.ejecutar(solicitudes.codigoBloqueo(codigo));
        return ResponseEntity.noContent().build();
    }
}
