package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.CerrarFolioRequest;
import co.edu.uniquindio.sga.application.dto.request.RegistrarPagoRequest;
import co.edu.uniquindio.sga.application.dto.request.RegistrarServicioAdicionalRequest;
import co.edu.uniquindio.sga.application.dto.response.FolioResponse;
import co.edu.uniquindio.sga.application.dto.response.MovimientoCargoResponse;
import co.edu.uniquindio.sga.application.dto.response.MovimientoPagoResponse;
import co.edu.uniquindio.sga.application.usecase.CerrarFolioUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarFolioPropioUseCase;
import co.edu.uniquindio.sga.application.usecase.ConsultarFolioUseCase;
import co.edu.uniquindio.sga.application.usecase.RegistrarPagoUseCase;
import co.edu.uniquindio.sga.application.usecase.RegistrarServicioAdicionalUseCase;
import co.edu.uniquindio.sga.application.usecase.RevertirCargoUseCase;
import co.edu.uniquindio.sga.application.usecase.RevertirPagoUseCase;
import co.edu.uniquindio.sga.domain.valueobject.AutorizacionCierre;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Consulta el folio, registra pagos y servicios, revierte un movimiento y lo cierra. El cierre exige X-Autor y el motivo.
 */

@RestController
@Validated
@RequestMapping("/api/folios")
@PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
public class FolioRestController {

    private final ConsultarFolioUseCase consultar;
    private final ConsultarFolioPropioUseCase consultarPropio;
    private final RegistrarPagoUseCase registrarPago;
    private final RegistrarServicioAdicionalUseCase registrarServicio;
    private final RevertirCargoUseCase revertirCargo;
    private final RevertirPagoUseCase revertirPago;
    private final CerrarFolioUseCase cerrar;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;

    public FolioRestController(ConsultarFolioUseCase consultar, ConsultarFolioPropioUseCase consultarPropio,
                               RegistrarPagoUseCase registrarPago,
                               RegistrarServicioAdicionalUseCase registrarServicio,
                               RevertirCargoUseCase revertirCargo, RevertirPagoUseCase revertirPago,
                               CerrarFolioUseCase cerrar, RespuestaApiMapper respuestas,
                               SolicitudApiMapper solicitudes) {
        this.consultar = consultar;
        this.consultarPropio = consultarPropio;
        this.registrarPago = registrarPago;
        this.registrarServicio = registrarServicio;
        this.revertirCargo = revertirCargo;
        this.revertirPago = revertirPago;
        this.cerrar = cerrar;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
    }

    @GetMapping("/{codigoReserva}")
    public FolioResponse consultar(@PathVariable String codigoReserva) {
        return respuestas.folio(consultar.ejecutar(solicitudes.codigoReserva(codigoReserva)));
    }

    @GetMapping("/{codigoReserva}/propio")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION','HUESPED')")
    public FolioResponse consultarPropio(@PathVariable String codigoReserva, @RequestParam String codigoTitular) {
        return respuestas.folio(consultarPropio.ejecutar(
                solicitudes.codigoReserva(codigoReserva), solicitudes.codigoTitular(codigoTitular)));
    }

    @PostMapping("/{codigoReserva}/pagos")
    public ResponseEntity<MovimientoPagoResponse> registrarPago(@PathVariable String codigoReserva,
                                                                @Valid @RequestBody RegistrarPagoRequest request) {
        var movimiento = registrarPago.ejecutar(
                solicitudes.codigoReserva(codigoReserva),
                solicitudes.dinero(request.monto()),
                request.medioPago(),
                request.fecha());
        return RespuestasHttp.creado("/api/folios/" + codigoReserva, respuestas.movimientoPago(movimiento));
    }

    @PostMapping("/{codigoReserva}/servicios")
    public ResponseEntity<MovimientoCargoResponse> registrarServicio(@PathVariable String codigoReserva,
                                                                     @Valid @RequestBody RegistrarServicioAdicionalRequest request) {
        var movimiento = registrarServicio.ejecutar(
                solicitudes.codigoReserva(codigoReserva), request.nombreServicio(), request.fecha());
        return RespuestasHttp.creado("/api/folios/" + codigoReserva, respuestas.movimientoCargo(movimiento));
    }

    @PostMapping("/{codigoReserva}/cargos/{numero}/reverso")
    public MovimientoCargoResponse revertirCargo(@PathVariable String codigoReserva,
                                                 @PathVariable int numero,
                                                 @RequestParam LocalDate fecha) {
        return respuestas.movimientoCargo(revertirCargo.ejecutar(solicitudes.codigoReserva(codigoReserva), numero, fecha));
    }

    @PostMapping("/{codigoReserva}/pagos/{numero}/reverso")
    public MovimientoPagoResponse revertirPago(@PathVariable String codigoReserva,
                                               @PathVariable int numero,
                                               @RequestParam LocalDate fecha) {
        return respuestas.movimientoPago(revertirPago.ejecutar(solicitudes.codigoReserva(codigoReserva), numero, fecha));
    }

    @PostMapping("/{codigoReserva}/cierre")
    public ResponseEntity<Void> cerrar(@PathVariable String codigoReserva,
                                       @Valid @RequestBody CerrarFolioRequest request,
                                       @RequestHeader(value = "X-Autor", defaultValue = "administrador") String autor) {
        cerrar.ejecutar(solicitudes.codigoReserva(codigoReserva), new AutorizacionCierre(autor, request.motivoAutorizacion()));
        return ResponseEntity.noContent().build();
    }
}
