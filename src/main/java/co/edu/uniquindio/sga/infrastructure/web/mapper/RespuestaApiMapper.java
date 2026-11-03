package co.edu.uniquindio.sga.infrastructure.web.mapper;

import co.edu.uniquindio.sga.application.dto.response.AlojamientoResponse;
import co.edu.uniquindio.sga.application.dto.response.ApartamentoResponse;
import co.edu.uniquindio.sga.application.dto.response.BloqueoResponse;
import co.edu.uniquindio.sga.application.dto.response.CanalExternoResponse;
import co.edu.uniquindio.sga.application.dto.response.CargoResponse;
import co.edu.uniquindio.sga.application.dto.response.ConfiguracionAlojamientoResponse;
import co.edu.uniquindio.sga.application.dto.response.ConflictoCanalResponse;
import co.edu.uniquindio.sga.application.dto.response.CotizacionResponse;
import co.edu.uniquindio.sga.application.dto.response.DesgloseNocheResponse;
import co.edu.uniquindio.sga.application.dto.response.FolioResponse;
import co.edu.uniquindio.sga.application.dto.response.IngresoPorMedioResponse;
import co.edu.uniquindio.sga.application.dto.response.ReporteIngresosResponse;
import co.edu.uniquindio.sga.application.dto.response.ReporteOcupacionResponse;
import co.edu.uniquindio.sga.application.dto.response.ReporteReservasPorCanalResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservasPorCanalResponse;
import co.edu.uniquindio.sga.application.dto.response.LiquidacionPoliticaResponse;
import co.edu.uniquindio.sga.application.dto.response.ModificacionReservaResponse;
import co.edu.uniquindio.sga.application.dto.response.MovimientoCargoResponse;
import co.edu.uniquindio.sga.application.dto.response.MovimientoPagoResponse;
import co.edu.uniquindio.sga.application.dto.response.NovedadResponse;
import co.edu.uniquindio.sga.application.dto.response.OcupanteResponse;
import co.edu.uniquindio.sga.application.dto.response.PagoResponse;
import co.edu.uniquindio.sga.application.dto.response.PeriodoAnualResponse;
import co.edu.uniquindio.sga.application.dto.response.PoliticaCancelacionResponse;
import co.edu.uniquindio.sga.application.dto.response.RecordatorioResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaDetalleResponse;
import co.edu.uniquindio.sga.application.dto.response.ReservaResumenResponse;
import co.edu.uniquindio.sga.application.dto.response.ResultadoConciliacionResponse;
import co.edu.uniquindio.sga.application.dto.response.ServicioAdicionalResponse;
import co.edu.uniquindio.sga.application.dto.response.TarifaResponse;
import co.edu.uniquindio.sga.application.dto.response.TemporadaResponse;
import co.edu.uniquindio.sga.application.dto.response.TitularResponse;
import co.edu.uniquindio.sga.application.dto.response.TramoCancelacionResponse;
import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.entity.ConflictoCanal;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Novedad;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Recordatorio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService;
import co.edu.uniquindio.sga.domain.service.LiquidacionPolitica;
import co.edu.uniquindio.sga.domain.valueobject.Cargo;
import co.edu.uniquindio.sga.domain.valueobject.IndicadorIngresos;
import co.edu.uniquindio.sga.domain.valueobject.IndicadorOcupacion;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoReporte;
import co.edu.uniquindio.sga.domain.valueobject.ReservasDeCanal;
import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.Cotizacion;
import co.edu.uniquindio.sga.domain.valueobject.Pago;
import co.edu.uniquindio.sga.domain.valueobject.ResultadoMovimientoFolio;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper de presentación: entidades y VOs de dominio → DTO de respuesta de la API.
 */
@Component
public class RespuestaApiMapper {

    private static final String COP = "COP";

    public TitularResponse titular(Titular titular) {
        return new TitularResponse(titular.getCodigo().valor(), titular.getNombre(),
                titular.getFechaNacimiento(), titular.getTelefono(), titular.getCorreo());
    }

    public ApartamentoResponse apartamento(Apartamento apartamento) {
        return new ApartamentoResponse(
                apartamento.getIdentificacion().valor(),
                apartamento.getNombre(),
                apartamento.getDescripcion(),
                apartamento.getDormitorios(),
                apartamento.getCapacidad().valor(),
                apartamento.getDotacion(),
                apartamento.getGaleria().urls(),
                apartamento.getGaleria().principal(),
                apartamento.getEstadoOperativo(),
                apartamento.estaActivo());
    }

    public AlojamientoResponse alojamiento(Alojamiento alojamiento) {
        return new AlojamientoResponse(
                alojamiento.getCodigo().valor(),
                alojamiento.getNombre(),
                alojamiento.getDescripcion(),
                alojamiento.getUbicacion().ciudad(),
                alojamiento.getUbicacion().direccion(),
                alojamiento.getUbicacion().latitud(),
                alojamiento.getUbicacion().longitud(),
                configuracion(alojamiento.getConfiguracion()),
                alojamiento.getNormasConvivencia(),
                alojamiento.getServiciosAdicionales().stream()
                        .map(s -> new ServicioAdicionalResponse(s.nombre(), s.valor().valor().longValue(), s.generaCargo()))
                        .toList());
    }

    public ConfiguracionAlojamientoResponse configuracion(ConfiguracionAlojamiento cfg) {
        return new ConfiguracionAlojamientoResponse(
                cfg.horaEntrada(),
                cfg.horaSalida(),
                cfg.tiempoPreparacion().valor().toHours(),
                cfg.plazoConfirmacion().horas(),
                cfg.horaLimiteNoShow(),
                cfg.umbralEdadFacturable().anios(),
                cfg.porcentajeAnticipo().valor(),
                cfg.antelacionRecordatorioEntrada().toHours(),
                cfg.antelacionRecordatorioSalida().toHours());
    }

    public TemporadaResponse temporada(Temporada temporada) {
        return new TemporadaResponse(
                temporada.getId().valor(),
                temporada.getNombre(),
                temporada.esBase(),
                temporada.getPeriodos().stream()
                        .map(p -> new PeriodoAnualResponse(
                                p.inicio().getMonthValue(), p.inicio().getDayOfMonth(),
                                p.fin().getMonthValue(), p.fin().getDayOfMonth()))
                        .toList());
    }

    public ReservaDetalleResponse reservaDetalle(Reserva reserva) {
        return new ReservaDetalleResponse(
                reserva.getCodigo().valor(),
                reserva.getEstado(),
                reserva.getCanalOrigen(),
                reserva.getIdentificadorExterno() == null ? null : reserva.getIdentificadorExterno().valor(),
                reserva.getTitular().valor(),
                reserva.getApartamento().valor(),
                reserva.getEstancia().entrada(),
                reserva.getEstancia().salida(),
                reserva.getEstancia().numeroNoches(),
                reserva.getHoraLlegadaEstimada(),
                reserva.getOcupantes().stream()
                        .map(o -> new OcupanteResponse(o.nombre(), o.fechaNacimiento()))
                        .toList(),
                reserva.totalOcupantes(),
                reserva.getValorCongelado().valor().longValue(),
                COP,
                reserva.getPoliticaVersionCongelada().numero(),
                reserva.getFechaCreacion(),
                reserva.getRegistro() == null ? null : reserva.getRegistro().momento(),
                reserva.getSalida() == null ? null : reserva.getSalida().momento(),
                reserva.getMotivoCancelacion() == null ? null : reserva.getMotivoCancelacion().descripcion(),
                reserva.getMascotas().cantidad());
    }

    public ReservaResumenResponse reservaResumen(Reserva reserva) {
        return new ReservaResumenResponse(
                reserva.getCodigo().valor(),
                reserva.getEstado(),
                reserva.getApartamento().valor(),
                reserva.getEstancia().entrada(),
                reserva.getEstancia().salida(),
                reserva.getTitular().valor(),
                reserva.totalOcupantes(),
                reserva.getValorCongelado().valor().longValue());
    }

    public FolioResponse folio(Folio folio) {
        return new FolioResponse(
                folio.getCodigo().valor(),
                folio.getReserva().valor(),
                folio.getCargos().stream().map(this::cargo).toList(),
                folio.getPagos().stream().map(this::pago).toList(),
                folio.totalCargos().valor().longValue(),
                folio.totalPagos().valor().longValue(),
                folio.calcularSaldo().valor().longValue(),
                COP,
                folio.estaCerrado(),
                folio.getAutorizacionCierre() == null ? null : folio.getAutorizacionCierre().autor(),
                folio.getAutorizacionCierre() == null ? null : folio.getAutorizacionCierre().motivo());
    }

    public CargoResponse cargo(Cargo cargo) {
        return new CargoResponse(cargo.numero(), cargo.tipo(), cargo.concepto(),
                cargo.monto().valor().longValue(), cargo.fecha(), cargo.esReverso(), cargo.revierteA());
    }

    public PagoResponse pago(Pago pago) {
        return new PagoResponse(pago.numero(), pago.monto().valor().longValue(), pago.medio(),
                pago.fecha(), pago.esReverso(), pago.revierteA());
    }

    public BloqueoResponse bloqueo(Bloqueo bloqueo) {
        return new BloqueoResponse(
                bloqueo.getCodigo().valor(),
                bloqueo.getApartamento().valor(),
                bloqueo.getRango().entrada(),
                bloqueo.getRango().salida(),
                bloqueo.getMotivo().descripcion(),
                bloqueo.getFechaRegistro(),
                bloqueo.estaVigente());
    }

    public CanalExternoResponse canal(CanalExterno canal) {
        return new CanalExternoResponse(canal.getCodigo().valor(), canal.getTipo(), canal.puedeVender());
    }

    public ConflictoCanalResponse conflicto(ConflictoCanal conflicto) {
        return new ConflictoCanalResponse(
                conflicto.getCodigo().valor(),
                conflicto.getCanal(),
                conflicto.getIdentificadorExterno().valor(),
                conflicto.getApartamento().valor(),
                conflicto.getEstanciaSolicitada().entrada(),
                conflicto.getEstanciaSolicitada().salida(),
                conflicto.getReservaVigente().valor(),
                conflicto.getFechaDeteccion(),
                conflicto.estaRevisado(),
                conflicto.getNotaRevision());
    }

    public NovedadResponse novedad(Novedad novedad) {
        return new NovedadResponse(
                novedad.getCodigo().valor(),
                novedad.getApartamento().valor(),
                novedad.getFecha(),
                novedad.getAutor(),
                novedad.getDescripcion(),
                novedad.getGravedad(),
                novedad.estaResuelta(),
                novedad.getFechaResolucion());
    }

    public PoliticaCancelacionResponse politica(PoliticaCancelacion politica) {
        return new PoliticaCancelacionResponse(
                politica.getCodigo().valor(),
                politica.getVersion().numero(),
                politica.estaVigente(),
                politica.getTramos().stream()
                        .map(t -> new TramoCancelacionResponse(t.diasAntelacionMinima(), t.retencion().valor()))
                        .toList(),
                politica.getRetencionNoShow().valor(),
                politica.getCreadaEn());
    }

    public RecordatorioResponse recordatorio(Recordatorio recordatorio) {
        return new RecordatorioResponse(
                recordatorio.getCodigo().valor(),
                recordatorio.getReserva().valor(),
                recordatorio.getTipo(),
                recordatorio.getMomentoEvento(),
                recordatorio.getFechaRecordatorio(),
                recordatorio.getEstado(),
                recordatorio.getUltimoError());
    }

    public CotizacionResponse cotizacion(Cotizacion cotizacion) {
        return new CotizacionResponse(
                cotizacion.numeroNoches(),
                cotizacion.total().valor().longValue(),
                COP,
                cotizacion.desglose().stream()
                        .map(d -> new DesgloseNocheResponse(d.noche().fecha(), d.temporada().valor(),
                                d.tarifaAplicada().valor().longValue(), d.ocupantesFacturables(),
                                d.subtotal().valor().longValue()))
                        .toList());
    }

    public TarifaResponse tarifa(Tarifa tarifa) {
        return new TarifaResponse(tarifa.temporada().valor(), tarifa.valorPorOcupante().valor().longValue());
    }

    public LiquidacionPoliticaResponse liquidacion(LiquidacionPolitica liquidacion) {
        return new LiquidacionPoliticaResponse(
                liquidacion.reserva().valor(),
                liquidacion.retencion().valor().longValue(),
                liquidacion.saldoFolio().valor().longValue(),
                COP);
    }

    public ModificacionReservaResponse modificacion(String codigoReserva, BigDecimal ajuste) {
        return new ModificacionReservaResponse(codigoReserva, ajuste.longValue(), COP);
    }

    public MovimientoCargoResponse movimientoCargo(ResultadoMovimientoFolio<Cargo> resultado) {
        return new MovimientoCargoResponse(
                resultado.reserva().valor(),
                cargo(resultado.movimiento()),
                resultado.saldo().valor().longValue(),
                COP);
    }

    public MovimientoPagoResponse movimientoPago(ResultadoMovimientoFolio<Pago> resultado) {
        return new MovimientoPagoResponse(
                resultado.reserva().valor(),
                pago(resultado.movimiento()),
                resultado.saldo().valor().longValue(),
                COP);
    }

    public ResultadoConciliacionResponse conciliacion(ConciliacionCanalExternoService.ResultadoConciliacion resultado) {
        return new ResultadoConciliacionResponse(
                resultado.tipo().name(),
                resultado.reserva() == null ? null : reservaDetalle(resultado.reserva()),
                resultado.conflicto() == null ? null : conflicto(resultado.conflicto()));
    }

    public ReporteIngresosResponse ingresos(IndicadorIngresos indicador) {
        return new ReporteIngresosResponse(
                indicador.periodo().desde(),
                indicador.periodo().hasta(),
                indicador.total().longValue(),
                COP,
                indicador.porMedio().entrySet().stream()
                        .map(entrada -> new IngresoPorMedioResponse(entrada.getKey(), entrada.getValue().longValue()))
                        .toList());
    }

    public ReporteOcupacionResponse ocupacion(IndicadorOcupacion indicador) {
        return new ReporteOcupacionResponse(
                indicador.periodo().desde(),
                indicador.periodo().hasta(),
                indicador.nochesVendidas(),
                indicador.apartamentosActivos(),
                indicador.nochesPeriodo(),
                indicador.porcentaje());
    }

    public ReporteReservasPorCanalResponse reservasPorCanal(PeriodoReporte periodo, List<ReservasDeCanal> canales) {
        return new ReporteReservasPorCanalResponse(
                periodo.desde(),
                periodo.hasta(),
                canales.stream()
                        .map(canal -> new ReservasPorCanalResponse(canal.canal(), canal.total(), canal.canceladas(), canal.noShow()))
                        .toList());
    }

    public List<ReservaResumenResponse> reservasResumen(List<Reserva> reservas) {
        return reservas.stream().map(this::reservaResumen).toList();
    }
}
