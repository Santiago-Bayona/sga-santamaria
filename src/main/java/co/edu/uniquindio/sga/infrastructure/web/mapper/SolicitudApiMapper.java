package co.edu.uniquindio.sga.infrastructure.web.mapper;

import co.edu.uniquindio.sga.application.dto.request.ActualizarConfiguracionAlojamientoRequest;
import co.edu.uniquindio.sga.application.dto.request.ActualizarDatosAlojamientoRequest;
import co.edu.uniquindio.sga.application.dto.request.AgregarServicioAdicionalAlojamientoRequest;
import co.edu.uniquindio.sga.application.dto.request.CrearTemporadaRequest;
import co.edu.uniquindio.sga.application.dto.request.ListarReservasRequest;
import co.edu.uniquindio.sga.application.dto.request.OcupanteRequest;
import co.edu.uniquindio.sga.application.dto.request.PeriodoAnualRequest;
import co.edu.uniquindio.sga.application.dto.request.TarifaApartamentoRequest;
import co.edu.uniquindio.sga.application.dto.request.TramoCancelacionRequest;
import co.edu.uniquindio.sga.application.usecase.FiltrosDisponibilidad;
import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.valueobject.Capacidad;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import co.edu.uniquindio.sga.domain.valueobject.CodigoNovedad;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.GaleriaImagenes;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.Mascotas;
import co.edu.uniquindio.sga.domain.valueobject.MotivoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.MotivoCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoAnual;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoReporte;
import co.edu.uniquindio.sga.domain.valueobject.PlazoConfirmacion;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.ServicioAdicional;
import co.edu.uniquindio.sga.domain.valueobject.TiempoPreparacion;
import co.edu.uniquindio.sga.domain.valueobject.TramoCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.Ubicacion;
import co.edu.uniquindio.sga.domain.valueobject.UmbralEdadFacturable;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Convierte DTOs de entrada en tipos del dominio. Los casos de uso no ven los DTO.
 */
@Mapper(componentModel = "spring")
public interface SolicitudApiMapper {

    Ocupante ocupante(OcupanteRequest request);

    List<Ocupante> ocupantes(List<OcupanteRequest> requests);

    default CodigoTitular codigoTitular(String valor) {
        return enBlanco(valor) ? null : new CodigoTitular(valor);
    }

    default CodigoReserva codigoReserva(String valor) {
        return new CodigoReserva(valor);
    }

    default CodigoBloqueo codigoBloqueo(String valor) {
        return new CodigoBloqueo(valor);
    }

    default CodigoCanalExterno codigoCanal(String valor) {
        return new CodigoCanalExterno(valor);
    }

    default CodigoNovedad codigoNovedad(String valor) {
        return new CodigoNovedad(valor);
    }

    default IdentificacionApartamento identificacion(String valor) {
        return enBlanco(valor) ? null : new IdentificacionApartamento(valor);
    }

    default IdentificadorExterno identificadorExterno(String valor) {
        return enBlanco(valor) ? null : new IdentificadorExterno(valor);
    }

    default IdTemporada idTemporada(String valor) {
        return new IdTemporada(valor);
    }

    default Estancia estancia(LocalDate entrada, LocalDate salida) {
        return new Estancia(entrada, salida);
    }

    default Dinero dinero(BigDecimal valor) {
        return valor == null ? null : new Dinero(valor);
    }

    default Dinero dinero(Long pesos) {
        return pesos == null ? null : Dinero.pesos(pesos);
    }

    default Mascotas mascotas(Integer cantidad) {
        return cantidad == null ? Mascotas.NINGUNA : new Mascotas(cantidad);
    }

    default Mascotas mascotasOpcional(Integer cantidad) {
        return cantidad == null ? null : new Mascotas(cantidad);
    }

    default Capacidad capacidad(Integer valor) {
        return new Capacidad(valor);
    }

    default GaleriaImagenes galeria(List<String> imagenes, Integer indicePrincipal) {
        return new GaleriaImagenes(imagenes, indicePrincipal);
    }

    default MotivoCancelacion motivoCancelacion(String motivo) {
        return new MotivoCancelacion(motivo);
    }

    default MotivoBloqueo motivoBloqueo(String motivo) {
        return new MotivoBloqueo(motivo);
    }

    default Porcentaje porcentaje(Integer valor) {
        return new Porcentaje(valor);
    }

    default PeriodoReporte periodo(LocalDate desde, LocalDate hasta) {
        return new PeriodoReporte(desde, hasta);
    }

    default Ubicacion ubicacion(ActualizarDatosAlojamientoRequest request) {
        return new Ubicacion(request.ciudad(), request.direccion(), request.latitud(), request.longitud());
    }

    default ConfiguracionAlojamiento configuracion(ActualizarConfiguracionAlojamientoRequest request) {
        return new ConfiguracionAlojamiento(
                request.horaEntrada(),
                request.horaSalida(),
                new TiempoPreparacion(Duration.ofMinutes(request.tiempoPreparacionMinutos())),
                new PlazoConfirmacion(request.plazoConfirmacionHoras()),
                request.horaLimiteNoShow(),
                new UmbralEdadFacturable(request.umbralEdadFacturable()),
                new Porcentaje(request.porcentajeAnticipo()),
                Duration.ofHours(request.antelacionRecordatorioEntradaHoras()),
                Duration.ofHours(request.antelacionRecordatorioSalidaHoras()));
    }

    default ServicioAdicional servicio(AgregarServicioAdicionalAlojamientoRequest request) {
        return new ServicioAdicional(request.nombre(), Dinero.pesos(request.valor()), request.generaCargo());
    }

    default TramoCancelacion tramo(TramoCancelacionRequest request) {
        return new TramoCancelacion(request.diasAntelacionMinima(), new Porcentaje(request.porcentajeRetencion()));
    }

    default List<TramoCancelacion> tramos(List<TramoCancelacionRequest> requests) {
        return requests.stream().map(this::tramo).toList();
    }

    default PeriodoAnual periodo(PeriodoAnualRequest request) {
        return new PeriodoAnual(
                MonthDay.of(request.mesInicio(), request.diaInicio()),
                MonthDay.of(request.mesFin(), request.diaFin()));
    }

    default Temporada temporada(CrearTemporadaRequest request) {
        IdTemporada id = new IdTemporada(request.idTemporada());
        if (Boolean.TRUE.equals(request.base())) {
            return Temporada.base(id, request.nombre());
        }
        return Temporada.especial(id, request.nombre(), request.periodos().stream().map(this::periodo).toList());
    }

    default Map<IdentificacionApartamento, Dinero> tarifas(List<TarifaApartamentoRequest> tarifas) {
        Map<IdentificacionApartamento, Dinero> mapa = new LinkedHashMap<>();
        for (TarifaApartamentoRequest tarifa : tarifas) {
            mapa.put(new IdentificacionApartamento(tarifa.identificacionApartamento()), dinero(tarifa.valorPorOcupante()));
        }
        return mapa;
    }

    default CriterioBusquedaReserva criterio(ListarReservasRequest request) {
        if (request == null) {
            return CriterioBusquedaReserva.sinFiltros();
        }
        return new CriterioBusquedaReserva(
                request.fechaDesde(),
                request.fechaHasta(),
                request.estado(),
                identificacion(request.identificacionApartamento()),
                request.canalOrigen(),
                codigoTitular(request.codigoTitular()));
    }

    default FiltrosDisponibilidad filtros(Integer capacidadMinima, BigDecimal precioMinimo, BigDecimal precioMaximo,
                                          List<String> caracteristicas, List<String> servicios) {
        return new FiltrosDisponibilidad(capacidadMinima, dinero(precioMinimo), dinero(precioMaximo),
                caracteristicas, servicios);
    }

    default List<Ocupante> ocupantesPorNacimiento(List<LocalDate> fechas) {
        List<Ocupante> ocupantes = new ArrayList<>();
        for (int i = 0; i < fechas.size(); i++) {
            ocupantes.add(new Ocupante("Ocupante " + (i + 1), fechas.get(i)));
        }
        return ocupantes;
    }

    private static boolean enBlanco(String valor) {
        return valor == null || valor.isBlank();
    }
}
