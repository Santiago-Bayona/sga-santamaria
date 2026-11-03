package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.ActualizarApartamentoRequest;
import co.edu.uniquindio.sga.application.dto.request.CambiarCapacidadApartamentoRequest;
import co.edu.uniquindio.sga.application.dto.request.CrearApartamentoRequest;
import co.edu.uniquindio.sga.application.dto.request.DefinirTarifaRequest;
import co.edu.uniquindio.sga.application.dto.response.ApartamentoResponse;
import co.edu.uniquindio.sga.application.dto.response.PaginaResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaResumenResponse;
import co.edu.uniquindio.sga.application.dto.response.TarifaResponse;
import co.edu.uniquindio.sga.application.usecase.ActivarApartamentoUseCase;
import co.edu.uniquindio.sga.application.usecase.ActualizarApartamentoUseCase;
import co.edu.uniquindio.sga.application.usecase.CambiarCapacidadApartamentoUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarApartamentoUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarApartamentosActivosUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarPendientesPreparacionUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarTarifasApartamentoUseCase;
import co.edu.uniquindio.sga.application.usecase.CrearApartamentoUseCase;
import co.edu.uniquindio.sga.application.usecase.DeclararFueraDeServicioUseCase;
import co.edu.uniquindio.sga.application.usecase.DefinirTarifaUseCase;
import co.edu.uniquindio.sga.application.usecase.FinalizarPreparacionUseCase;
import co.edu.uniquindio.sga.application.usecase.IniciarPreparacionUseCase;
import co.edu.uniquindio.sga.application.usecase.ReintegrarAlServicioUseCase;
import co.edu.uniquindio.sga.application.usecase.RetirarApartamentoUseCase;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Ciclo del apartamento: alta, consulta paginada, tarifas, capacidad, preparacion, retiro y reintegro. El identificador va en el path.
 */

@RestController
@Validated
@RequestMapping("/api/apartamentos")
public class ApartamentoRestController {

    private final CrearApartamentoUseCase crear;
    private final ConsultarApartamentoUseCase consultar;
    private final ConsultarApartamentosActivosUseCase activos;
    private final ActualizarApartamentoUseCase actualizar;
    private final ActivarApartamentoUseCase activar;
    private final RetirarApartamentoUseCase retirar;
    private final CambiarCapacidadApartamentoUseCase cambiarCapacidad;
    private final DefinirTarifaUseCase definirTarifa;
    private final ConsultarTarifasApartamentoUseCase tarifas;
    private final ConsultarPendientesPreparacionUseCase pendientes;
    private final IniciarPreparacionUseCase iniciarPreparacion;
    private final FinalizarPreparacionUseCase finalizarPreparacion;
    private final DeclararFueraDeServicioUseCase fueraDeServicio;
    private final ReintegrarAlServicioUseCase reintegrar;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;
    private final Clock reloj;

    public ApartamentoRestController(CrearApartamentoUseCase crear, ConsultarApartamentoUseCase consultar,
                                     ConsultarApartamentosActivosUseCase activos,
                                     ActualizarApartamentoUseCase actualizar, ActivarApartamentoUseCase activar,
                                     RetirarApartamentoUseCase retirar,
                                     CambiarCapacidadApartamentoUseCase cambiarCapacidad,
                                     DefinirTarifaUseCase definirTarifa,
                                     ConsultarTarifasApartamentoUseCase tarifas,
                                     ConsultarPendientesPreparacionUseCase pendientes,
                                     IniciarPreparacionUseCase iniciarPreparacion,
                                     FinalizarPreparacionUseCase finalizarPreparacion,
                                     DeclararFueraDeServicioUseCase fueraDeServicio,
                                     ReintegrarAlServicioUseCase reintegrar,
                                     RespuestaApiMapper respuestas, SolicitudApiMapper solicitudes, Clock reloj) {
        this.crear = crear;
        this.consultar = consultar;
        this.activos = activos;
        this.actualizar = actualizar;
        this.activar = activar;
        this.retirar = retirar;
        this.cambiarCapacidad = cambiarCapacidad;
        this.definirTarifa = definirTarifa;
        this.tarifas = tarifas;
        this.pendientes = pendientes;
        this.iniciarPreparacion = iniciarPreparacion;
        this.finalizarPreparacion = finalizarPreparacion;
        this.fueraDeServicio = fueraDeServicio;
        this.reintegrar = reintegrar;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
        this.reloj = reloj;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApartamentoResponse> crear(@Valid @RequestBody CrearApartamentoRequest request) {
        var apartamento = crear.ejecutar(
                solicitudes.identificacion(request.identificacionApartamento()),
                request.nombre(),
                request.descripcion(),
                request.dormitorios(),
                solicitudes.capacidad(request.capacidad()),
                solicitudes.galeria(request.imagenes(), request.indiceImagenPrincipal()),
                request.dotacion());
        String ruta = "/api/apartamentos/" + apartamento.getIdentificacion().valor();
        return RespuestasHttp.creado(ruta, respuestas.apartamento(apartamento));
    }

    @GetMapping
    public PaginaResponse<ApartamentoResponse> activos(@RequestParam(defaultValue = "0") int pagina) {
        var resultado = activos.ejecutar(pagina);
        return PaginaResponse.desde(resultado.contenido().stream().map(respuestas::apartamento).toList(),
                resultado.numero(), resultado.total());
    }

    @GetMapping("/pendientes-preparacion")
    public PaginaResponse<ApartamentoResponse> pendientes(@RequestParam(defaultValue = "0") int pagina) {
        var resultado = pendientes.ejecutar(pagina);
        return PaginaResponse.desde(resultado.contenido().stream().map(respuestas::apartamento).toList(),
                resultado.numero(), resultado.total());
    }

    @GetMapping("/{identificacion}")
    public ApartamentoResponse consultar(@PathVariable String identificacion) {
        return respuestas.apartamento(consultar.ejecutar(solicitudes.identificacion(identificacion)));
    }

    @PutMapping("/{identificacion}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApartamentoResponse actualizar(@PathVariable String identificacion,
                                          @Valid @RequestBody ActualizarApartamentoRequest request) {
        return respuestas.apartamento(actualizar.ejecutar(
                solicitudes.identificacion(identificacion),
                request.nombre(),
                request.descripcion(),
                request.dormitorios(),
                solicitudes.galeria(request.imagenes(), request.indiceImagenPrincipal()),
                request.dotacion()));
    }

    @PostMapping("/{identificacion}/activacion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activar(@PathVariable String identificacion) {
        activar.ejecutar(solicitudes.identificacion(identificacion));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{identificacion}/retiro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> retirar(@PathVariable String identificacion) {
        retirar.ejecutar(solicitudes.identificacion(identificacion));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{identificacion}/capacidad")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReservaResumenResponse> cambiarCapacidad(@PathVariable String identificacion,
                                                         @Valid @RequestBody CambiarCapacidadApartamentoRequest request) {
        return respuestas.reservasResumen(cambiarCapacidad.ejecutar(
                solicitudes.identificacion(identificacion), solicitudes.capacidad(request.nuevaCapacidad())));
    }

    @PostMapping("/{identificacion}/tarifas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> definirTarifa(@PathVariable String identificacion,
                                              @Valid @RequestBody DefinirTarifaRequest request) {
        definirTarifa.ejecutar(
                solicitudes.identificacion(identificacion),
                solicitudes.idTemporada(request.idTemporada()),
                solicitudes.dinero(request.valorPorOcupante()),
                LocalDateTime.now(reloj));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{identificacion}/tarifas")
    public List<TarifaResponse> tarifas(@PathVariable String identificacion) {
        return tarifas.ejecutar(solicitudes.identificacion(identificacion)).stream().map(respuestas::tarifa).toList();
    }

    @PostMapping("/{identificacion}/preparacion/inicio")
    @PreAuthorize("hasAnyRole('ADMIN','SERVICIO')")
    public ResponseEntity<Void> iniciarPreparacion(@PathVariable String identificacion) {
        iniciarPreparacion.ejecutar(solicitudes.identificacion(identificacion));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{identificacion}/preparacion/fin")
    @PreAuthorize("hasAnyRole('ADMIN','SERVICIO')")
    public ResponseEntity<Void> finalizarPreparacion(@PathVariable String identificacion) {
        finalizarPreparacion.ejecutar(solicitudes.identificacion(identificacion));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{identificacion}/fuera-de-servicio")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReservaResumenResponse> fueraDeServicio(@PathVariable String identificacion) {
        return respuestas.reservasResumen(fueraDeServicio.ejecutar(solicitudes.identificacion(identificacion)));
    }

    @PostMapping("/{identificacion}/reintegracion")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reintegrar(@PathVariable String identificacion) {
        reintegrar.ejecutar(solicitudes.identificacion(identificacion));
        return ResponseEntity.noContent().build();
    }
}
