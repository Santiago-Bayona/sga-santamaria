package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.ActualizarConfiguracionAlojamientoRequest;
import co.edu.uniquindio.sga.application.dto.request.ActualizarDatosAlojamientoRequest;
import co.edu.uniquindio.sga.application.dto.request.AgregarServicioAdicionalAlojamientoRequest;
import co.edu.uniquindio.sga.application.dto.request.CrearTemporadaRequest;
import co.edu.uniquindio.sga.application.dto.request.DefinirNormasConvivenciaRequest;
import co.edu.uniquindio.sga.application.dto.response.AlojamientoResponse;
import co.edu.uniquindio.sga.application.dto.response.TemporadaResponse;
import co.edu.uniquindio.sga.application.usecase.ActualizarConfiguracionAlojamientoUseCase;
import co.edu.uniquindio.sga.application.usecase.ActualizarDatosAlojamientoUseCase;
import co.edu.uniquindio.sga.application.usecase.AgregarServicioAdicionalAlojamientoUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarAlojamientoUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarTemporadasUseCase;
import co.edu.uniquindio.sga.application.usecase.CrearTemporadaUseCase;
import co.edu.uniquindio.sga.application.usecase.DefinirNormasConvivenciaUseCase;
import co.edu.uniquindio.sga.application.usecase.RetirarTemporadaUseCase;
import co.edu.uniquindio.sga.infrastructure.web.RespuestasHttp;
import co.edu.uniquindio.sga.infrastructure.web.mapper.RespuestaApiMapper;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Ficha publica del alojamiento y comandos de administrador: datos, configuración, normas, servicios y temporadas.
 */

@RestController
@Validated
@RequestMapping("/api/alojamiento")
public class AlojamientoRestController {

    private final ConsultarAlojamientoUseCase consultar;
    private final ActualizarDatosAlojamientoUseCase actualizarDatos;
    private final ActualizarConfiguracionAlojamientoUseCase actualizarConfiguracion;
    private final DefinirNormasConvivenciaUseCase normas;
    private final AgregarServicioAdicionalAlojamientoUseCase servicio;
    private final ConsultarTemporadasUseCase temporadas;
    private final CrearTemporadaUseCase crearTemporada;
    private final RetirarTemporadaUseCase retirarTemporada;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;
    private final Clock reloj;

    public AlojamientoRestController(ConsultarAlojamientoUseCase consultar,
                                     ActualizarDatosAlojamientoUseCase actualizarDatos,
                                     ActualizarConfiguracionAlojamientoUseCase actualizarConfiguracion,
                                     DefinirNormasConvivenciaUseCase normas,
                                     AgregarServicioAdicionalAlojamientoUseCase servicio,
                                     ConsultarTemporadasUseCase temporadas,
                                     CrearTemporadaUseCase crearTemporada,
                                     RetirarTemporadaUseCase retirarTemporada,
                                     RespuestaApiMapper respuestas,
                                     SolicitudApiMapper solicitudes,
                                     Clock reloj) {
        this.consultar = consultar;
        this.actualizarDatos = actualizarDatos;
        this.actualizarConfiguracion = actualizarConfiguracion;
        this.normas = normas;
        this.servicio = servicio;
        this.temporadas = temporadas;
        this.crearTemporada = crearTemporada;
        this.retirarTemporada = retirarTemporada;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
        this.reloj = reloj;
    }

    @GetMapping
    public AlojamientoResponse consultar() {
        return respuestas.alojamiento(consultar.ejecutar());
    }

    @PutMapping("/datos")
    @PreAuthorize("hasRole('ADMIN')")
    public AlojamientoResponse actualizarDatos(@Valid @RequestBody ActualizarDatosAlojamientoRequest request) {
        return respuestas.alojamiento(actualizarDatos.ejecutar(request.nombre(), request.descripcion(), solicitudes.ubicacion(request)));
    }

    @PutMapping("/configuracion")
    @PreAuthorize("hasRole('ADMIN')")
    public AlojamientoResponse actualizarConfiguracion(
            @Valid @RequestBody ActualizarConfiguracionAlojamientoRequest request) {
        return respuestas.alojamiento(actualizarConfiguracion.ejecutar(solicitudes.configuracion(request)));
    }

    @PutMapping("/normas")
    @PreAuthorize("hasRole('ADMIN')")
    public AlojamientoResponse normas(@Valid @RequestBody DefinirNormasConvivenciaRequest request) {
        return respuestas.alojamiento(normas.ejecutar(request.normas()));
    }

    @PostMapping("/servicios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlojamientoResponse> servicio(@Valid @RequestBody AgregarServicioAdicionalAlojamientoRequest request) {
        var alojamiento = servicio.ejecutar(solicitudes.servicio(request));
        return RespuestasHttp.creado("/api/alojamiento", respuestas.alojamiento(alojamiento));
    }

    @GetMapping("/temporadas")
    public List<TemporadaResponse> temporadas() {
        return temporadas.ejecutar().stream().map(respuestas::temporada).toList();
    }

    @PostMapping("/temporadas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> crearTemporada(@Valid @RequestBody CrearTemporadaRequest request) {
        crearTemporada.ejecutar(solicitudes.temporada(request), solicitudes.tarifas(request.tarifas()), LocalDateTime.now(reloj));
        return ResponseEntity.created(java.net.URI.create("/api/alojamiento/temporadas/" + request.idTemporada())).build();
    }

    @PostMapping("/temporadas/{idTemporada}/retiro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> retirarTemporada(@PathVariable String idTemporada) {
        retirarTemporada.ejecutar(solicitudes.idTemporada(idTemporada));
        return ResponseEntity.noContent().build();
    }
}
