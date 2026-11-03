package co.edu.uniquindio.sga.infrastructure.web.controller;

import co.edu.uniquindio.sga.application.dto.response.ApartamentoDisponibleResponse;
import co.edu.uniquindio.sga.application.dto.response.PaginaResponse;
import co.edu.uniquindio.sga.application.usecase.ConsultarDisponibilidadUseCase;
import co.edu.uniquindio.sga.infrastructure.web.mapper.SolicitudApiMapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * GET /api/disponibilidad, publico. Fechas, nacimientos y filtros opcionales. Pagina de a 10.
 */

@RestController
@Validated
@RequestMapping("/api/disponibilidad")
public class DisponibilidadRestController {

    private final ConsultarDisponibilidadUseCase disponibilidad;
    private final SolicitudApiMapper solicitudes;

    public DisponibilidadRestController(ConsultarDisponibilidadUseCase disponibilidad, SolicitudApiMapper solicitudes) {
        this.disponibilidad = disponibilidad;
        this.solicitudes = solicitudes;
    }

    @GetMapping
    public PaginaResponse<ApartamentoDisponibleResponse> consultar(
            @RequestParam @NotNull LocalDate fechaEntrada,
            @RequestParam @NotNull LocalDate fechaSalida,
            @RequestParam @NotEmpty List<@NotNull @Past LocalDate> fechasNacimiento,
            @RequestParam(required = false) Integer capacidadMinima,
            @RequestParam(required = false) BigDecimal precioMinimo,
            @RequestParam(required = false) BigDecimal precioMaximo,
            @RequestParam(required = false) List<String> caracteristicas,
            @RequestParam(required = false) List<String> servicios,
            @RequestParam(defaultValue = "0") int pagina) {
        var resultado = disponibilidad.ejecutar(
                solicitudes.estancia(fechaEntrada, fechaSalida),
                solicitudes.ocupantesPorNacimiento(fechasNacimiento),
                solicitudes.filtros(capacidadMinima, precioMinimo, precioMaximo, caracteristicas, servicios),
                pagina);
        return PaginaResponse.desde(resultado.contenido(), resultado.numero(), resultado.total());
    }
}
