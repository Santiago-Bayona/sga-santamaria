package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.ActualizarTitularRequest;
import co.edu.uniquindio.sga.application.dto.request.RegistrarTitularRequest;
import co.edu.uniquindio.sga.application.dto.response.TitularResponse;
import co.edu.uniquindio.sga.application.usecase.ActualizarTitularUseCase;
import co.edu.uniquindio.sga.application.usecase.RegistrarTitularUseCase;
import co.edu.uniquindio.sga.infrastructure.web.RespuestasHttp;
import co.edu.uniquindio.sga.infrastructure.web.mapper.RespuestaApiMapper;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Alta y actualización de titulares. El 201 apunta a /api/titulares/{código}.
 */

@RestController
@Validated
@RequestMapping("/api/titulares")
@PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
public class TitularRestController {

    private final RegistrarTitularUseCase registrar;
    private final ActualizarTitularUseCase actualizar;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;

    public TitularRestController(RegistrarTitularUseCase registrar, ActualizarTitularUseCase actualizar,
                                 RespuestaApiMapper respuestas, SolicitudApiMapper solicitudes) {
        this.registrar = registrar;
        this.actualizar = actualizar;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
    }

    @PostMapping
    public ResponseEntity<TitularResponse> registrar(@Valid @RequestBody RegistrarTitularRequest request) {
        var titular = registrar.ejecutar(
                solicitudes.codigoTitular(request.codigoTitular()),
                request.nombre(),
                request.fechaNacimiento(),
                request.telefono(),
                request.correo());
        return RespuestasHttp.creado("/api/titulares/" + titular.getCodigo().valor(), respuestas.titular(titular));
    }

    @PutMapping("/{codigo}")
    public TitularResponse actualizar(@PathVariable String codigo, @Valid @RequestBody ActualizarTitularRequest request) {
        return respuestas.titular(actualizar.ejecutar(solicitudes.codigoTitular(codigo), request.telefono(), request.correo()));
    }
}
