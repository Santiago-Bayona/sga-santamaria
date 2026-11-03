package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.ConciliarReservaExternaRequest;
import co.edu.uniquindio.sga.application.dto.request.RegistrarCanalExternoRequest;
import co.edu.uniquindio.sga.application.dto.request.RotarCredencialCanalExternoRequest;
import co.edu.uniquindio.sga.application.dto.response.CanalExternoResponse;
import co.edu.uniquindio.sga.application.dto.response.ConflictoCanalResponse;
import co.edu.uniquindio.sga.application.dto.response.LiquidacionPoliticaResponse;
import co.edu.uniquindio.sga.application.dto.response.PaginaResponse;
import co.edu.uniquindio.sga.application.dto.response.ResultadoConciliacionResponse;
import co.edu.uniquindio.sga.application.usecase.ActivarCanalExternoUseCase;
import co.edu.uniquindio.sga.application.usecase.CancelarReservaExternaUseCase;
import co.edu.uniquindio.sga.application.usecase.ConciliarReservaExternaUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarCanalesExternosUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarConflictosCanalUseCase;
import co.edu.uniquindio.sga.application.usecase.DesactivarCanalExternoUseCase;
import co.edu.uniquindio.sga.application.usecase.RegistrarCanalExternoUseCase;
import co.edu.uniquindio.sga.application.usecase.RotarCredencialCanalExternoUseCase;
import co.edu.uniquindio.sga.infrastructure.web.RespuestasHttp;
import co.edu.uniquindio.sga.infrastructure.web.adapter.CredencialCanalExternoAdapter;
import co.edu.uniquindio.sga.infrastructure.web.mapper.RespuestaApiMapper;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Administra canales y, con la cabecera X-Canal-Credencial, concilia o cancela reservas externas. El código del canal va en el path.
 */

@RestController
@Validated
@RequestMapping("/api/canales")
@PreAuthorize("hasRole('ADMIN')")
public class CanalExternoRestController {

    private final RegistrarCanalExternoUseCase registrar;
    private final ConsultarCanalesExternosUseCase consultar;
    private final ActivarCanalExternoUseCase activar;
    private final DesactivarCanalExternoUseCase desactivar;
    private final RotarCredencialCanalExternoUseCase rotar;
    private final ConciliarReservaExternaUseCase conciliar;
    private final CancelarReservaExternaUseCase cancelarExterna;
    private final ConsultarConflictosCanalUseCase conflictos;
    private final CredencialCanalExternoAdapter credenciales;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;
    private final Clock reloj;

    public CanalExternoRestController(RegistrarCanalExternoUseCase registrar,
                                      ConsultarCanalesExternosUseCase consultar,
                                      ActivarCanalExternoUseCase activar,
                                      DesactivarCanalExternoUseCase desactivar,
                                      RotarCredencialCanalExternoUseCase rotar,
                                      ConciliarReservaExternaUseCase conciliar,
                                      CancelarReservaExternaUseCase cancelarExterna,
                                      ConsultarConflictosCanalUseCase conflictos,
                                      CredencialCanalExternoAdapter credenciales,
                                      RespuestaApiMapper respuestas,
                                      SolicitudApiMapper solicitudes,
                                      Clock reloj) {
        this.registrar = registrar;
        this.consultar = consultar;
        this.activar = activar;
        this.desactivar = desactivar;
        this.rotar = rotar;
        this.conciliar = conciliar;
        this.cancelarExterna = cancelarExterna;
        this.conflictos = conflictos;
        this.credenciales = credenciales;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
        this.reloj = reloj;
    }

    @PostMapping
    public ResponseEntity<CanalExternoResponse> registrar(@Valid @RequestBody RegistrarCanalExternoRequest request) {
        var canal = registrar.ejecutar(solicitudes.codigoCanal(request.codigoCanal()), request.tipo(), request.credencial());
        return RespuestasHttp.creado("/api/canales/" + canal.getCodigo().valor(), respuestas.canal(canal));
    }

    @GetMapping
    public PaginaResponse<CanalExternoResponse> listar(@RequestParam(defaultValue = "0") int pagina) {
        var resultado = consultar.ejecutar(pagina);
        return PaginaResponse.desde(resultado.contenido().stream().map(respuestas::canal).toList(),
                resultado.numero(), resultado.total());
    }

    @PostMapping("/{codigo}/activacion")
    public CanalExternoResponse activar(@PathVariable String codigo) {
        return respuestas.canal(activar.ejecutar(solicitudes.codigoCanal(codigo)));
    }

    @PostMapping("/{codigo}/desactivacion")
    public CanalExternoResponse desactivar(@PathVariable String codigo) {
        return respuestas.canal(desactivar.ejecutar(solicitudes.codigoCanal(codigo)));
    }

    @PostMapping("/{codigo}/credencial")
    public CanalExternoResponse rotar(@PathVariable String codigo,
                                      @Valid @RequestBody RotarCredencialCanalExternoRequest request) {
        return respuestas.canal(rotar.ejecutar(solicitudes.codigoCanal(codigo), request.nuevaCredencial()));
    }

    @PostMapping("/{codigo}/conciliaciones")
    @PreAuthorize("hasAnyRole('ADMIN','CANAL')")
    public ResultadoConciliacionResponse conciliar(@PathVariable String codigo,
                                                   @Valid @RequestBody ConciliarReservaExternaRequest request,
                                                   @RequestHeader("X-Canal-Credencial") String credencial) {
        credenciales.exigirCredencial(codigo, credencial);
        var resultado = conciliar.ejecutar(
                solicitudes.codigoCanal(codigo),
                solicitudes.identificadorExterno(request.identificadorExterno()),
                solicitudes.codigoTitular(request.codigoTitular()),
                solicitudes.identificacion(request.identificacionApartamento()),
                solicitudes.estancia(request.fechaEntrada(), request.fechaSalida()),
                solicitudes.ocupantes(request.acompanantes()),
                request.horaLlegadaEstimada(),
                LocalDateTime.now(reloj));
        return respuestas.conciliacion(resultado);
    }

    @PostMapping("/{codigo}/reservas/{identificadorExterno}/cancelacion")
    @PreAuthorize("hasAnyRole('ADMIN','CANAL')")
    public ResponseEntity<LiquidacionPoliticaResponse> cancelar(@PathVariable String codigo,
                                                                @PathVariable String identificadorExterno,
                                                                @RequestHeader("X-Canal-Credencial") String credencial) {
        credenciales.exigirCredencial(codigo, credencial);
        return cancelarExterna.ejecutar(
                        solicitudes.codigoCanal(codigo),
                        solicitudes.identificadorExterno(identificadorExterno),
                        LocalDateTime.now(reloj))
                .map(respuestas::liquidacion)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/conflictos")
    public PaginaResponse<ConflictoCanalResponse> conflictos(@RequestParam(defaultValue = "0") int pagina) {
        var resultado = conflictos.ejecutar(pagina);
        return PaginaResponse.desde(resultado.contenido().stream().map(respuestas::conflicto).toList(),
                resultado.numero(), resultado.total());
    }
}
