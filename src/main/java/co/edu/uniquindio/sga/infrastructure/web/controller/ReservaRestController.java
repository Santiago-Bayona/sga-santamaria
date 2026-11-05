package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.CancelarReservaRequest;
import co.edu.uniquindio.sga.application.dto.request.CrearReservaRequest;
import co.edu.uniquindio.sga.application.dto.request.ListarReservasRequest;
import co.edu.uniquindio.sga.application.dto.request.ModificarReservaRequest;
import co.edu.uniquindio.sga.application.dto.request.RegistrarSalidaRequest;
import co.edu.uniquindio.sga.application.dto.response.LiquidacionPoliticaResponse;
import co.edu.uniquindio.sga.application.dto.response.ModificacionReservaResponse;
import co.edu.uniquindio.sga.application.dto.response.PaginaResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaDetalleResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaResumenResponse;
import co.edu.uniquindio.sga.application.usecase.CancelarReservaUseCase;
import co.edu.uniquindio.sga.application.usecase.ConfirmarReservaUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarHistorialReservasUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarLlegadasPrevistasUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarReservaPorEstadoUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarReservaUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarSalidasPrevistasUseCase;
import co.edu.uniquindio.sga.application.usecase.CrearReservaUseCase;
import co.edu.uniquindio.sga.application.usecase.DeclararNoShowUseCase;
import co.edu.uniquindio.sga.application.usecase.ListarReservasUseCase;
import co.edu.uniquindio.sga.application.usecase.ModificarReservaUseCase;
import co.edu.uniquindio.sga.application.usecase.RegistrarLlegadaUseCase;
import co.edu.uniquindio.sga.application.usecase.RegistrarSalidaUseCase;
import co.edu.uniquindio.sga.application.usecase.VencerReservasPendientesUseCase;
import co.edu.uniquindio.sga.domain.valueobject.AutorizacionCierre;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Ciclo de la reserva. Las consultas son GET, el código va en el path y el alta responde 201 con Location. Recepción opera; el huésped ve su historial.
 */

@RestController
@Validated
@RequestMapping("/api/reservas")
@PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
public class ReservaRestController {

    private final CrearReservaUseCase crearReserva;
    private final ConsultarReservaUseCase consultarReserva;
    private final ConsultarReservaPorEstadoUseCase consultarPorEstado;
    private final ListarReservasUseCase listarReservas;
    private final ConsultarHistorialReservasUseCase historial;
    private final ConsultarLlegadasPrevistasUseCase llegadas;
    private final ConsultarSalidasPrevistasUseCase salidas;
    private final ConfirmarReservaUseCase confirmar;
    private final ModificarReservaUseCase modificar;
    private final CancelarReservaUseCase cancelar;
    private final RegistrarLlegadaUseCase llegada;
    private final RegistrarSalidaUseCase salida;
    private final DeclararNoShowUseCase noShow;
    private final VencerReservasPendientesUseCase vencer;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;
    private final Clock reloj;

    public ReservaRestController(CrearReservaUseCase crearReserva, ConsultarReservaUseCase consultarReserva,
                                 ConsultarReservaPorEstadoUseCase consultarPorEstado,
                                 ListarReservasUseCase listarReservas, ConsultarHistorialReservasUseCase historial,
                                 ConsultarLlegadasPrevistasUseCase llegadas, ConsultarSalidasPrevistasUseCase salidas,
                                 ConfirmarReservaUseCase confirmar, ModificarReservaUseCase modificar,
                                 CancelarReservaUseCase cancelar, RegistrarLlegadaUseCase llegada,
                                 RegistrarSalidaUseCase salida, DeclararNoShowUseCase noShow,
                                 VencerReservasPendientesUseCase vencer, RespuestaApiMapper respuestas,
                                 SolicitudApiMapper solicitudes, Clock reloj) {
        this.crearReserva = crearReserva;
        this.consultarReserva = consultarReserva;
        this.consultarPorEstado = consultarPorEstado;
        this.listarReservas = listarReservas;
        this.historial = historial;
        this.llegadas = llegadas;
        this.salidas = salidas;
        this.confirmar = confirmar;
        this.modificar = modificar;
        this.cancelar = cancelar;
        this.llegada = llegada;
        this.salida = salida;
        this.noShow = noShow;
        this.vencer = vencer;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
        this.reloj = reloj;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION') or (hasRole('HUESPED') and "
            + "@propietario.puedeReservarComoHuesped(#request.codigoTitular(), #request.canalOrigen(), authentication))")
    public ResponseEntity<ReservaDetalleResponse> crear(@Valid @RequestBody CrearReservaRequest request) {
        var reserva = crearReserva.ejecutar(
                solicitudes.codigoTitular(request.codigoTitular()),
                solicitudes.identificacion(request.identificacionApartamento()),
                solicitudes.estancia(request.fechaEntrada(), request.fechaSalida()),
                solicitudes.ocupantes(request.acompanantes()),
                solicitudes.mascotas(request.mascotas()),
                request.horaLlegadaEstimada(),
                request.canalOrigen(),
                solicitudes.identificadorExterno(request.identificadorExterno()),
                LocalDateTime.now(reloj));
        return RespuestasHttp.creado("/api/reservas/" + reserva.getCodigo().valor(), respuestas.reservaDetalle(reserva));
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION') or (hasRole('HUESPED') and @propietario.esTitularDeReserva(#codigo, authentication))")
    public ReservaDetalleResponse consultar(@PathVariable String codigo) {
        return respuestas.reservaDetalle(consultarReserva.ejecutar(solicitudes.codigoReserva(codigo)));
    }

    @GetMapping
    public PaginaResponse<ReservaResumenResponse> listar(@Valid ListarReservasRequest filtros,
                                                         @RequestParam(defaultValue = "0") int pagina) {
        var resultado = listarReservas.ejecutar(solicitudes.criterio(filtros), pagina);
        return PaginaResponse.desde(respuestas.reservasResumen(resultado.contenido()), resultado.numero(), resultado.total());
    }

    @GetMapping("/estado/{estado}")
    public PaginaResponse<ReservaResumenResponse> porEstado(@PathVariable EstadoReserva estado,
                                                            @RequestParam(defaultValue = "0") int pagina) {
        var resultado = consultarPorEstado.ejecutar(estado, pagina);
        return PaginaResponse.desde(respuestas.reservasResumen(resultado.contenido()), resultado.numero(), resultado.total());
    }

    @GetMapping("/titular/{codigoTitular}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION') or (hasRole('HUESPED') and @propietario.esTitular(#codigoTitular, authentication))")
    public PaginaResponse<ReservaResumenResponse> historialTitular(@PathVariable String codigoTitular,
                                                                   @RequestParam(defaultValue = "0") int pagina) {
        var resultado = historial.ejecutar(solicitudes.codigoTitular(codigoTitular), pagina);
        return PaginaResponse.desde(respuestas.reservasResumen(resultado.contenido()), resultado.numero(), resultado.total());
    }

    @GetMapping("/llegadas")
    public PaginaResponse<ReservaResumenResponse> llegadas(@RequestParam LocalDate fecha,
                                                           @RequestParam(defaultValue = "0") int pagina) {
        var resultado = llegadas.ejecutar(fecha, pagina);
        return PaginaResponse.desde(respuestas.reservasResumen(resultado.contenido()), resultado.numero(), resultado.total());
    }

    @GetMapping("/salidas")
    public PaginaResponse<ReservaResumenResponse> salidas(@RequestParam LocalDate fecha,
                                                          @RequestParam(defaultValue = "0") int pagina) {
        var resultado = salidas.ejecutar(fecha, pagina);
        return PaginaResponse.desde(respuestas.reservasResumen(resultado.contenido()), resultado.numero(), resultado.total());
    }

    @PostMapping("/{codigo}/confirmacion")
    public ReservaDetalleResponse confirmar(@PathVariable String codigo) {
        return respuestas.reservaDetalle(confirmar.ejecutar(solicitudes.codigoReserva(codigo), LocalDateTime.now(reloj)));
    }

    @PutMapping("/{codigo}")
    public ModificacionReservaResponse modificar(@PathVariable String codigo,
                                                 @Valid @RequestBody ModificarReservaRequest request) {
        var ajuste = modificar.ejecutar(
                solicitudes.codigoReserva(codigo),
                solicitudes.identificacion(request.identificacionApartamento()),
                solicitudes.estancia(request.fechaEntrada(), request.fechaSalida()),
                solicitudes.ocupantes(request.acompanantes()),
                solicitudes.mascotasOpcional(request.mascotas()),
                LocalDateTime.now(reloj));
        return respuestas.modificacion(codigo, ajuste);
    }

    @PostMapping("/{codigo}/cancelacion")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION') or (hasRole('HUESPED') and @propietario.esTitularDeReserva(#codigo, authentication))")
    public LiquidacionPoliticaResponse cancelar(@PathVariable String codigo,
                                                @Valid @RequestBody CancelarReservaRequest request) {
        return respuestas.liquidacion(cancelar.ejecutar(
                solicitudes.codigoReserva(codigo),
                solicitudes.motivoCancelacion(request.motivo()),
                LocalDateTime.now(reloj)));
    }

    @PostMapping("/{codigo}/llegada")
    public ResponseEntity<Void> llegada(@PathVariable String codigo) {
        llegada.ejecutar(solicitudes.codigoReserva(codigo), LocalDateTime.now(reloj));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{codigo}/salida")
    public ResponseEntity<Void> salida(@PathVariable String codigo,
                                       @Valid @RequestBody RegistrarSalidaRequest request,
                                       @RequestHeader(value = "X-Autor", defaultValue = "sistema") String autor) {
        salida.ejecutar(solicitudes.codigoReserva(codigo),
                new AutorizacionCierre(autor, request.motivoAutorizacion()),
                LocalDateTime.now(reloj));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{codigo}/no-show")
    public LiquidacionPoliticaResponse noShow(@PathVariable String codigo) {
        return respuestas.liquidacion(noShow.ejecutar(solicitudes.codigoReserva(codigo), LocalDateTime.now(reloj)));
    }

    @PostMapping("/vencer-pendientes")
    public java.util.List<ReservaResumenResponse> vencerPendientes() {
        return respuestas.reservasResumen(vencer.ejecutar(LocalDateTime.now(reloj)));
    }
}
