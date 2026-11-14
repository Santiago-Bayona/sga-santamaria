package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.RegistrarNovedadRequest;
import co.edu.uniquindio.sga.application.dto.response.NovedadResponse;
import co.edu.uniquindio.sga.application.dto.response.PaginaResponse;
import co.edu.uniquindio.sga.application.usecase.ConsultarNovedadesUseCase;
import co.edu.uniquindio.sga.application.usecase.RegistrarNovedadUseCase;
import co.edu.uniquindio.sga.infrastructure.web.RespuestasHttp;
import co.edu.uniquindio.sga.infrastructure.web.mapper.RespuestaApiMapper;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Registra y consulta novedades de un apartamento. El autor llega en la cabecera X-Autor.
 */

@RestController
@Validated
@RequestMapping("/api/novedades")
public class NovedadRestController {

    private final RegistrarNovedadUseCase registrar;
    private final ConsultarNovedadesUseCase consultar;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;
    private final Clock reloj;

    public NovedadRestController(RegistrarNovedadUseCase registrar, ConsultarNovedadesUseCase consultar,
                                 RespuestaApiMapper respuestas, SolicitudApiMapper solicitudes, Clock reloj) {
        this.registrar = registrar;
        this.consultar = consultar;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
        this.reloj = reloj;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SERVICIO')")
    public ResponseEntity<NovedadResponse> registrar(@Valid @RequestBody RegistrarNovedadRequest request,
                                                     @RequestHeader(value = "X-Autor", defaultValue = "personal") String autor) {
        var novedad = registrar.ejecutar(
                solicitudes.codigoNovedad(request.codigoNovedad()),
                solicitudes.identificacion(request.identificacionApartamento()),
                request.descripcion(),
                request.gravedad(),
                autor,
                LocalDateTime.now(reloj));
        return RespuestasHttp.creado("/api/novedades/" + novedad.getCodigo().valor(), respuestas.novedad(novedad));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SERVICIO')")
    public PaginaResponse<NovedadResponse> consultar(@RequestParam @NotBlank String identificacion,
                                                     @RequestParam(defaultValue = "0") int pagina) {
        var resultado = consultar.ejecutar(solicitudes.identificacion(identificacion), pagina);
        return PaginaResponse.desde(resultado.contenido().stream().map(respuestas::novedad).toList(),
                resultado.numero(), resultado.total());
    }
}
