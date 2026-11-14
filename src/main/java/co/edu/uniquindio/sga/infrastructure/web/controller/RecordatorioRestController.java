package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.request.ProgramarRecordatorioRequest;
import co.edu.uniquindio.sga.application.dto.response.RecordatorioResponse;
import co.edu.uniquindio.sga.application.usecase.ConsultarRecordatoriosUseCase;
import co.edu.uniquindio.sga.application.usecase.ProgramarRecordatorioUseCase;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Programa y lista recordatorios de una reserva. El código de la reserva va en el path.
 */

@RestController
@Validated
@RequestMapping("/api/reservas/{codigoReserva}/recordatorios")
public class RecordatorioRestController {

    private final ProgramarRecordatorioUseCase programar;
    private final ConsultarRecordatoriosUseCase consultar;
    private final RespuestaApiMapper respuestas;
    private final SolicitudApiMapper solicitudes;

    public RecordatorioRestController(ProgramarRecordatorioUseCase programar, ConsultarRecordatoriosUseCase consultar,
                                      RespuestaApiMapper respuestas, SolicitudApiMapper solicitudes) {
        this.programar = programar;
        this.consultar = consultar;
        this.respuestas = respuestas;
        this.solicitudes = solicitudes;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
    public ResponseEntity<RecordatorioResponse> programar(@PathVariable String codigoReserva,
                                                          @Valid @RequestBody ProgramarRecordatorioRequest request) {
        var recordatorio = programar.ejecutar(solicitudes.codigoReserva(codigoReserva), request.tipo());
        return RespuestasHttp.creado("/api/reservas/" + codigoReserva + "/recordatorios", respuestas.recordatorio(recordatorio));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCION')")
    public List<RecordatorioResponse> listar(@PathVariable String codigoReserva) {
        return consultar.ejecutar(solicitudes.codigoReserva(codigoReserva)).stream().map(respuestas::recordatorio).toList();
    }
}
