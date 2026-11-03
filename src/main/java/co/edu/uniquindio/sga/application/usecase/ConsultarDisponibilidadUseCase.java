package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.dto.response.ApartamentoDisponibleResponse;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.service.CotizadorEstanciaService;
import co.edu.uniquindio.sga.domain.service.DisponibilidadApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Apartamentos disponibles para un rango y un grupo, con su valor estimado.
 */

@Service
public class ConsultarDisponibilidadUseCase {

    private final DisponibilidadApartamentoService disponibilidadService;
    private final CotizadorEstanciaService cotizadorService;
    private final AlojamientoRepository alojamientoRepository;

    public ConsultarDisponibilidadUseCase(DisponibilidadApartamentoService disponibilidadService,
                                          CotizadorEstanciaService cotizadorService,
                                          AlojamientoRepository alojamientoRepository) {
        this.disponibilidadService = disponibilidadService;
        this.cotizadorService = cotizadorService;
        this.alojamientoRepository = alojamientoRepository;
    }

    /**
     * Apartamentos disponibles para la estancia y el grupo, con los filtros de 7.5, paginados.
     * Los filtros se aplican después de calcular la disponibilidad real (RN-01, RN-20, bloqueos y preparación).
     * El rango de precio se compara contra el valor estimado de la estancia completa.
     */
    @Transactional(readOnly = true)
    public Pagina<ApartamentoDisponibleResponse> ejecutar(Estancia estancia, List<Ocupante> grupo,
                                                          FiltrosDisponibilidad filtros, int pagina) {

        List<Apartamento> disponibles = disponibilidadService.buscarDisponibles(estancia, grupo.size());

        // Los servicios adicionales los define el alojamiento, no cada apartamento.
        boolean ofreceServicios = ofreceTodos(filtros.servicios());

        List<ApartamentoDisponibleResponse> resultado = disponibles.stream()
                .filter(apartamento -> ofreceServicios)
                .filter(apartamento -> filtros.capacidadMinima() == null
                        || apartamento.getCapacidad().valor() >= filtros.capacidadMinima())
                .filter(apartamento -> tieneCaracteristicas(apartamento, filtros.caracteristicas()))
                .map(apartamento -> convertirRespuesta(apartamento, estancia, grupo))
                .filter(respuesta -> dentroDelRango(respuesta.valorEstimado(), filtros))
                .toList();

        return PaginacionEnMemoria.de(resultado, pagina);
    }

    private boolean ofreceTodos(List<String> solicitados) {
        if (solicitados.isEmpty()) {
            return true;
        }
        List<String> ofrecidos = alojamientoRepository.obtener().getServiciosAdicionales().stream()
                .map(servicio -> servicio.nombre().toLowerCase())
                .toList();
        return solicitados.stream().allMatch(servicio -> ofrecidos.contains(servicio.toLowerCase()));
    }

    private static boolean tieneCaracteristicas(Apartamento apartamento, List<String> requeridas) {
        List<String> dotacion = apartamento.getDotacion().stream().map(String::toLowerCase).toList();
        return requeridas.stream().allMatch(caracteristica -> dotacion.contains(caracteristica.toLowerCase()));
    }

    private static boolean dentroDelRango(long valorEstimado, FiltrosDisponibilidad filtros) {
        boolean sobreElMinimo = filtros.precioMinimo() == null
                || BigDecimal.valueOf(valorEstimado).compareTo(filtros.precioMinimo().valor()) >= 0;
        boolean bajoElMaximo = filtros.precioMaximo() == null
                || BigDecimal.valueOf(valorEstimado).compareTo(filtros.precioMaximo().valor()) <= 0;
        return sobreElMinimo && bajoElMaximo;
    }

    private ApartamentoDisponibleResponse convertirRespuesta(Apartamento apartamento, Estancia estancia,
                                                             List<Ocupante> grupo) {
        // El dominio calcula el valor noche por noche (RN-05) y rechaza un grupo sin ocupantes facturables.
        long valorEstimado = cotizadorService
                .cotizar(apartamento.getIdentificacion(), estancia, grupo)
                .total().valor().longValue();

        return new ApartamentoDisponibleResponse(
                apartamento.getIdentificacion().valor(),
                apartamento.getNombre(),
                apartamento.getCapacidad().valor(),
                apartamento.getDormitorios(),
                apartamento.getGaleria().principal(),
                valorEstimado
        );
    }
}
