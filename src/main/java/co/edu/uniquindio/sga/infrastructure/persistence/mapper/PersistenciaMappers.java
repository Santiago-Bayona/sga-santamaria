package co.edu.uniquindio.sga.infrastructure.persistence.mapper;

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
import co.edu.uniquindio.sga.domain.valueobject.AutorizacionCierre;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.Capacidad;
import co.edu.uniquindio.sga.domain.valueobject.Cargo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import co.edu.uniquindio.sga.domain.valueobject.CodigoConflictoCanal;
import co.edu.uniquindio.sga.domain.valueobject.CodigoFolio;
import co.edu.uniquindio.sga.domain.valueobject.CodigoNovedad;
import co.edu.uniquindio.sga.domain.valueobject.CodigoPolitica;
import co.edu.uniquindio.sga.domain.valueobject.CodigoRecordatorio;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.Cotizacion;
import co.edu.uniquindio.sga.domain.valueobject.DesgloseNoche;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.EstadoOperativo;
import co.edu.uniquindio.sga.domain.valueobject.EstadoRecordatorio;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.GaleriaImagenes;
import co.edu.uniquindio.sga.domain.valueobject.GravedadNovedad;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.MedioPago;
import co.edu.uniquindio.sga.domain.valueobject.MotivoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.Mascotas;
import co.edu.uniquindio.sga.domain.valueobject.MotivoCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.Noche;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import co.edu.uniquindio.sga.domain.valueobject.Pago;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoAnual;
import co.edu.uniquindio.sga.domain.valueobject.PlazoConfirmacion;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.Registro;
import co.edu.uniquindio.sga.domain.valueobject.RegistroTarifa;
import co.edu.uniquindio.sga.domain.valueobject.Salida;
import co.edu.uniquindio.sga.domain.valueobject.ServicioAdicional;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;
import co.edu.uniquindio.sga.domain.valueobject.TiempoPreparacion;
import co.edu.uniquindio.sga.domain.valueobject.TipoCargo;
import co.edu.uniquindio.sga.domain.valueobject.TipoRecordatorio;
import co.edu.uniquindio.sga.domain.valueobject.TramoCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.Ubicacion;
import co.edu.uniquindio.sga.domain.valueobject.UmbralEdadFacturable;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.AlojamientoJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.ApartamentoJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.BloqueoJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.CanalExternoJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.CargoEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.ConflictoCanalJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.DesgloseNocheEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.DineroEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.EstanciaEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.FolioJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.NovedadJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.OcupanteEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.PagoEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.PeriodoTemporadaEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.PoliticaCancelacionJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.RecordatorioJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.RegistroTarifaEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.ReservaJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.ServicioEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.TemporadaEmbeddable;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.TitularJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.TramoEmbeddable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper bidireccional dominio ↔ entidades JPA. El dominio permanece puro; aquí se materializan
 * los agregados para H2 y se rehidratan al volver a memoria.
 */
@Component
public class PersistenciaMappers {

    public TitularJpaEntity aEntidad(Titular titular) {
        TitularJpaEntity entidad = new TitularJpaEntity();
        entidad.setCodigo(titular.getCodigo().valor());
        entidad.setNombre(titular.getNombre());
        entidad.setFechaNacimiento(titular.getFechaNacimiento());
        entidad.setTelefono(titular.getTelefono());
        entidad.setCorreo(titular.getCorreo());
        return entidad;
    }

    public Titular aDominio(TitularJpaEntity entidad) {
        return new Titular(new CodigoTitular(entidad.getCodigo()), entidad.getNombre(),
                entidad.getFechaNacimiento(), entidad.getTelefono(), entidad.getCorreo());
    }

    public ApartamentoJpaEntity aEntidad(Apartamento apartamento) {
        ApartamentoJpaEntity entidad = new ApartamentoJpaEntity();
        entidad.setIdentificacion(apartamento.getIdentificacion().valor());
        entidad.setNombre(apartamento.getNombre());
        entidad.setDescripcion(apartamento.getDescripcion());
        entidad.setDormitorios(apartamento.getDormitorios());
        entidad.setCapacidad(apartamento.getCapacidad().valor());
        entidad.setEstadoOperativo(apartamento.getEstadoOperativo().name());
        entidad.setActivo(apartamento.estaActivo());
        entidad.setIndicePrincipal(apartamento.getGaleria().indicePrincipal());
        entidad.setImagenes(new ArrayList<>(apartamento.getGaleria().urls()));
        entidad.setDotacion(new ArrayList<>(apartamento.getDotacion()));
        entidad.setHistorialTarifas(apartamento.getHistorialTarifas().stream().map(h -> {
            RegistroTarifaEmbeddable registro = new RegistroTarifaEmbeddable();
            registro.setTemporada(h.tarifa().temporada().valor());
            registro.setValorPorOcupante(dinero(h.tarifa().valorPorOcupante()));
            registro.setVigenteDesde(h.vigenteDesde());
            return registro;
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        return entidad;
    }

    public Apartamento aDominio(ApartamentoJpaEntity entidad) {
        Apartamento apartamento = new Apartamento(
                new IdentificacionApartamento(entidad.getIdentificacion()),
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.getDormitorios(),
                new Capacidad(entidad.getCapacidad()),
                new GaleriaImagenes(entidad.getImagenes(), entidad.getIndicePrincipal()));

        if (entidad.getDotacion() != null && !entidad.getDotacion().isEmpty()) {
            apartamento.definirDotacion(entidad.getDotacion());
        }
        if (entidad.getHistorialTarifas() != null) {
            entidad.getHistorialTarifas().forEach(h -> apartamento.definirTarifa(
                    new Tarifa(new IdTemporada(h.getTemporada()), new Dinero(h.getValorPorOcupante().getValor())),
                    h.getVigenteDesde()));
        }

        if (entidad.isActivo()) {
            apartamento.activar(apartamento.getTarifas().stream().map(Tarifa::temporada).toList());
        }
        restaurarEstadoOperativo(apartamento, EstadoOperativo.valueOf(entidad.getEstadoOperativo()));
        return apartamento;
    }

    private static void restaurarEstadoOperativo(Apartamento apartamento, EstadoOperativo destino) {
        switch (destino) {
            case PREPARADO -> {
            }
            case OCUPADO -> apartamento.marcarOcupado();
            case PENDIENTE_PREPARACION -> {
                apartamento.marcarOcupado();
                apartamento.marcarPendientePreparacion();
            }
            case EN_PREPARACION -> {
                apartamento.marcarOcupado();
                apartamento.marcarPendientePreparacion();
                apartamento.marcarEnPreparacion();
            }
            case FUERA_DE_SERVICIO -> apartamento.declararFueraDeServicio();
        }
    }

    public AlojamientoJpaEntity aEntidad(Alojamiento alojamiento) {
        AlojamientoJpaEntity entidad = new AlojamientoJpaEntity();
        entidad.setCodigo(alojamiento.getCodigo().valor());
        entidad.setNombre(alojamiento.getNombre());
        entidad.setDescripcion(alojamiento.getDescripcion());
        entidad.setCiudad(alojamiento.getUbicacion().ciudad());
        entidad.setDireccion(alojamiento.getUbicacion().direccion());
        entidad.setLatitud(alojamiento.getUbicacion().latitud());
        entidad.setLongitud(alojamiento.getUbicacion().longitud());
        ConfiguracionAlojamiento cfg = alojamiento.getConfiguracion();
        entidad.setHoraEntrada(cfg.horaEntrada());
        entidad.setHoraSalida(cfg.horaSalida());
        entidad.setTiempoPreparacionHoras(cfg.tiempoPreparacion().valor().toHours());
        entidad.setPlazoConfirmacionHoras(cfg.plazoConfirmacion().horas());
        entidad.setHoraLimiteNoShow(cfg.horaLimiteNoShow());
        entidad.setUmbralEdadFacturable(cfg.umbralEdadFacturable().anios());
        entidad.setPorcentajeAnticipo(cfg.porcentajeAnticipo().valor());
        entidad.setAntelacionRecordatorioEntradaHoras(cfg.antelacionRecordatorioEntrada().toHours());
        entidad.setAntelacionRecordatorioSalidaHoras(cfg.antelacionRecordatorioSalida().toHours());
        entidad.setTemporadas(alojamiento.getTemporadas().stream().map(this::temporadaEmbeddable)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        entidad.setPeriodos(alojamiento.getTemporadas().stream()
                .flatMap(temporada -> temporada.getPeriodos().stream().map(periodo -> {
                    PeriodoTemporadaEmbeddable embeddable = new PeriodoTemporadaEmbeddable();
                    embeddable.setIdTemporada(temporada.getId().valor());
                    embeddable.setMesInicio(periodo.inicio().getMonthValue());
                    embeddable.setDiaInicio(periodo.inicio().getDayOfMonth());
                    embeddable.setMesFin(periodo.fin().getMonthValue());
                    embeddable.setDiaFin(periodo.fin().getDayOfMonth());
                    return embeddable;
                }))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        entidad.setNormas(new ArrayList<>(alojamiento.getNormasConvivencia()));
        entidad.setServicios(alojamiento.getServiciosAdicionales().stream().map(s -> {
            ServicioEmbeddable servicio = new ServicioEmbeddable();
            servicio.setNombre(s.nombre());
            servicio.setValor(dinero(s.valor()));
            servicio.setGeneraCargo(s.generaCargo());
            return servicio;
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        return entidad;
    }

    public Alojamiento aDominio(AlojamientoJpaEntity entidad) {
        List<Temporada> temporadas = entidad.getTemporadas().stream()
                .map(temporada -> temporadaDominio(temporada, entidad.getPeriodos()))
                .toList();
        ConfiguracionAlojamiento configuracion = new ConfiguracionAlojamiento(
                entidad.getHoraEntrada(),
                entidad.getHoraSalida(),
                new TiempoPreparacion(Duration.ofHours(entidad.getTiempoPreparacionHoras())),
                new PlazoConfirmacion(entidad.getPlazoConfirmacionHoras()),
                entidad.getHoraLimiteNoShow(),
                new UmbralEdadFacturable(entidad.getUmbralEdadFacturable()),
                new Porcentaje(entidad.getPorcentajeAnticipo()),
                Duration.ofHours(entidad.getAntelacionRecordatorioEntradaHoras()),
                Duration.ofHours(entidad.getAntelacionRecordatorioSalidaHoras()));
        Alojamiento alojamiento = new Alojamiento(
                new CodigoAlojamiento(entidad.getCodigo()),
                entidad.getNombre(),
                entidad.getDescripcion(),
                new Ubicacion(entidad.getCiudad(), entidad.getDireccion(), entidad.getLatitud(), entidad.getLongitud()),
                configuracion,
                temporadas);
        if (entidad.getNormas() != null && !entidad.getNormas().isEmpty()) {
            alojamiento.definirNormasConvivencia(entidad.getNormas());
        }
        if (entidad.getServicios() != null) {
            entidad.getServicios().forEach(s -> alojamiento.agregarServicioAdicional(
                    new ServicioAdicional(s.getNombre(), new Dinero(s.getValor().getValor()), s.isGeneraCargo())));
        }
        return alojamiento;
    }

    private TemporadaEmbeddable temporadaEmbeddable(Temporada temporada) {
        TemporadaEmbeddable embeddable = new TemporadaEmbeddable();
        embeddable.setIdTemporada(temporada.getId().valor());
        embeddable.setNombre(temporada.getNombre());
        embeddable.setBase(temporada.esBase());
        return embeddable;
    }

    private Temporada temporadaDominio(TemporadaEmbeddable temporada, List<PeriodoTemporadaEmbeddable> periodos) {
        if (temporada.isBase()) {
            return Temporada.base(new IdTemporada(temporada.getIdTemporada()), temporada.getNombre());
        }
        List<PeriodoAnual> delAnio = periodos.stream()
                .filter(periodo -> temporada.getIdTemporada().equals(periodo.getIdTemporada()))
                .map(periodo -> new PeriodoAnual(
                        MonthDay.of(periodo.getMesInicio(), periodo.getDiaInicio()),
                        MonthDay.of(periodo.getMesFin(), periodo.getDiaFin())))
                .toList();
        return Temporada.especial(new IdTemporada(temporada.getIdTemporada()), temporada.getNombre(), delAnio);
    }

    public ReservaJpaEntity aEntidad(Reserva reserva) {
        ReservaJpaEntity entidad = new ReservaJpaEntity();
        entidad.setCodigo(reserva.getCodigo().valor());
        entidad.setCodigoTitular(reserva.getTitular().valor());
        entidad.setNombreTitular(reserva.getOcupantes().getFirst().nombre());
        entidad.setNacimientoTitular(reserva.getOcupantes().getFirst().fechaNacimiento());
        entidad.setVersionPolitica(reserva.getPoliticaVersionCongelada().numero());
        entidad.setCanalOrigen(reserva.getCanalOrigen().name());
        entidad.setIdentificadorExterno(reserva.getIdentificadorExterno() == null
                ? null : reserva.getIdentificadorExterno().valor());
        entidad.setFechaCreacion(reserva.getFechaCreacion());
        entidad.setIdentificacionApartamento(reserva.getApartamento().valor());
        entidad.setEstancia(estancia(reserva.getEstancia()));
        entidad.setHoraLlegadaEstimada(reserva.getHoraLlegadaEstimada());
        entidad.setEstado(reserva.getEstado().name());
        entidad.setMascotas(reserva.getMascotas().cantidad());
        entidad.setMomentoRegistro(reserva.getRegistro() == null ? null : reserva.getRegistro().momento());
        entidad.setMomentoSalida(reserva.getSalida() == null ? null : reserva.getSalida().momento());
        entidad.setMotivoCancelacion(reserva.getMotivoCancelacion() == null
                ? null : reserva.getMotivoCancelacion().descripcion());
        entidad.setOcupantes(reserva.getOcupantes().stream()
                .map(o -> new OcupanteEmbeddable(o.nombre(), o.fechaNacimiento()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        entidad.setDesglose(reserva.getCotizacionCongelada().desglose().stream().map(d -> {
            DesgloseNocheEmbeddable noche = new DesgloseNocheEmbeddable();
            noche.setFecha(d.noche().fecha());
            noche.setTemporada(d.temporada().valor());
            noche.setTarifaAplicada(dinero(d.tarifaAplicada()));
            noche.setOcupantesFacturables(d.ocupantesFacturables());
            noche.setSubtotal(dinero(d.subtotal()));
            return noche;
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        return entidad;
    }

    public Reserva aDominio(ReservaJpaEntity entidad) {
        List<Ocupante> ocupantes = entidad.getOcupantes().stream()
                .map(o -> new Ocupante(o.getNombre(), o.getFechaNacimiento()))
                .toList();
        Cotizacion cotizacion = new Cotizacion(entidad.getDesglose().stream()
                .map(d -> new DesgloseNoche(new Noche(d.getFecha()), new IdTemporada(d.getTemporada()),
                        new Dinero(d.getTarifaAplicada().getValor()), d.getOcupantesFacturables(),
                        new Dinero(d.getSubtotal().getValor())))
                .toList());
        return Reserva.rehidratar(
                new CodigoReserva(entidad.getCodigo()),
                new CodigoTitular(entidad.getCodigoTitular()),
                ocupantes.getFirst(),
                new IdentificacionApartamento(entidad.getIdentificacionApartamento()),
                aEstancia(entidad.getEstancia()),
                ocupantes,
                entidad.getHoraLlegadaEstimada(),
                cotizacion,
                new VersionPolitica(entidad.getVersionPolitica()),
                CanalOrigen.valueOf(entidad.getCanalOrigen()),
                entidad.getIdentificadorExterno() == null ? null : new IdentificadorExterno(entidad.getIdentificadorExterno()),
                entidad.getFechaCreacion(),
                EstadoReserva.valueOf(entidad.getEstado()),
                entidad.getMomentoRegistro() == null ? null : new Registro(entidad.getMomentoRegistro()),
                entidad.getMomentoSalida() == null ? null : new Salida(entidad.getMomentoSalida()),
                entidad.getMotivoCancelacion() == null ? null : new MotivoCancelacion(entidad.getMotivoCancelacion()),
                new Mascotas(entidad.getMascotas()));
    }

    public FolioJpaEntity aEntidad(Folio folio) {
        FolioJpaEntity entidad = new FolioJpaEntity();
        entidad.setCodigo(folio.getCodigo().valor());
        entidad.setCodigoReserva(folio.getReserva().valor());
        entidad.setCerrado(folio.estaCerrado());
        entidad.setAutorCierre(folio.getAutorizacionCierre() == null ? null : folio.getAutorizacionCierre().autor());
        entidad.setMotivoCierre(folio.getAutorizacionCierre() == null ? null : folio.getAutorizacionCierre().motivo());
        entidad.setCargos(folio.getCargos().stream().map(c -> {
            CargoEmbeddable cargo = new CargoEmbeddable();
            cargo.setNumero(c.numero());
            cargo.setTipo(c.tipo().name());
            cargo.setConcepto(c.concepto());
            cargo.setMonto(dinero(c.monto()));
            cargo.setFecha(c.fecha());
            cargo.setResta(c.resta());
            cargo.setRevierteA(c.revierteA());
            return cargo;
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        entidad.setPagos(folio.getPagos().stream().map(p -> {
            PagoEmbeddable pago = new PagoEmbeddable();
            pago.setNumero(p.numero());
            pago.setMonto(dinero(p.monto()));
            pago.setMedio(p.medio().name());
            pago.setFecha(p.fecha());
            pago.setRevierteA(p.revierteA());
            return pago;
        }).collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        return entidad;
    }

    public Folio aDominio(FolioJpaEntity entidad) {
        List<Cargo> cargos = entidad.getCargos().stream()
                .map(c -> new Cargo(c.getNumero(), TipoCargo.valueOf(c.getTipo()), c.getConcepto(),
                        new Dinero(c.getMonto().getValor()), c.getFecha(), c.isResta(), c.getRevierteA()))
                .toList();
        List<Pago> pagos = entidad.getPagos() == null ? List.of() : entidad.getPagos().stream()
                .map(p -> new Pago(p.getNumero(), new Dinero(p.getMonto().getValor()), MedioPago.valueOf(p.getMedio()),
                        p.getFecha(), p.getRevierteA()))
                .toList();
        AutorizacionCierre autorizacion = entidad.getAutorCierre() == null || entidad.getAutorCierre().isBlank()
                ? null : new AutorizacionCierre(entidad.getAutorCierre(), entidad.getMotivoCierre());
        return Folio.rehidratar(new CodigoFolio(entidad.getCodigo()), new CodigoReserva(entidad.getCodigoReserva()),
                cargos, pagos, entidad.isCerrado(), autorizacion);
    }

    public BloqueoJpaEntity aEntidad(Bloqueo bloqueo) {
        BloqueoJpaEntity entidad = new BloqueoJpaEntity();
        entidad.setCodigo(bloqueo.getCodigo().valor());
        entidad.setIdentificacionApartamento(bloqueo.getApartamento().valor());
        entidad.setFechaRegistro(bloqueo.getFechaRegistro());
        entidad.setRango(estancia(bloqueo.getRango()));
        entidad.setMotivo(bloqueo.getMotivo().descripcion());
        entidad.setVigente(bloqueo.estaVigente());
        return entidad;
    }

    public Bloqueo aDominio(BloqueoJpaEntity entidad) {
        Bloqueo bloqueo = new Bloqueo(
                new CodigoBloqueo(entidad.getCodigo()),
                new IdentificacionApartamento(entidad.getIdentificacionApartamento()),
                aEstancia(entidad.getRango()),
                new MotivoBloqueo(entidad.getMotivo()),
                entidad.getFechaRegistro());
        if (!entidad.isVigente()) {
            bloqueo.levantar();
        }
        return bloqueo;
    }

    public CanalExternoJpaEntity aEntidad(CanalExterno canal) {
        CanalExternoJpaEntity entidad = new CanalExternoJpaEntity();
        entidad.setCodigo(canal.getCodigo().valor());
        entidad.setTipo(canal.getTipo());
        entidad.setCredencial(canal.getCredencial());
        entidad.setActivo(canal.puedeVender());
        return entidad;
    }

    public CanalExterno aDominio(CanalExternoJpaEntity entidad) {
        CanalExterno canal = new CanalExterno(new CodigoCanalExterno(entidad.getCodigo()),
                entidad.getTipo(), entidad.getCredencial());
        if (!entidad.isActivo()) {
            canal.desactivar();
        }
        return canal;
    }

    public ConflictoCanalJpaEntity aEntidad(ConflictoCanal conflicto) {
        ConflictoCanalJpaEntity entidad = new ConflictoCanalJpaEntity();
        entidad.setCodigo(conflicto.getCodigo().valor());
        entidad.setCanal(conflicto.getCanal().name());
        entidad.setIdentificadorExterno(conflicto.getIdentificadorExterno().valor());
        entidad.setIdentificacionApartamento(conflicto.getApartamento().valor());
        entidad.setEstancia(estancia(conflicto.getEstanciaSolicitada()));
        entidad.setCodigoReservaVigente(conflicto.getReservaVigente().valor());
        entidad.setFechaDeteccion(conflicto.getFechaDeteccion());
        entidad.setRevisado(conflicto.estaRevisado());
        entidad.setNotaRevision(conflicto.getNotaRevision());
        return entidad;
    }

    public ConflictoCanal aDominio(ConflictoCanalJpaEntity entidad) {
        ConflictoCanal conflicto = new ConflictoCanal(
                new CodigoConflictoCanal(entidad.getCodigo()),
                CanalOrigen.valueOf(entidad.getCanal()),
                new IdentificadorExterno(entidad.getIdentificadorExterno()),
                new IdentificacionApartamento(entidad.getIdentificacionApartamento()),
                aEstancia(entidad.getEstancia()),
                new CodigoReserva(entidad.getCodigoReservaVigente()),
                entidad.getFechaDeteccion());
        if (entidad.isRevisado()) {
            conflicto.marcarRevisado(entidad.getNotaRevision());
        }
        return conflicto;
    }

    public NovedadJpaEntity aEntidad(Novedad novedad) {
        NovedadJpaEntity entidad = new NovedadJpaEntity();
        entidad.setCodigo(novedad.getCodigo().valor());
        entidad.setIdentificacionApartamento(novedad.getApartamento().valor());
        entidad.setFecha(novedad.getFecha());
        entidad.setAutor(novedad.getAutor());
        entidad.setDescripcion(novedad.getDescripcion());
        entidad.setGravedad(novedad.getGravedad().name());
        entidad.setResuelta(novedad.estaResuelta());
        entidad.setFechaResolucion(novedad.getFechaResolucion());
        return entidad;
    }

    public Novedad aDominio(NovedadJpaEntity entidad) {
        Novedad novedad = new Novedad(
                new CodigoNovedad(entidad.getCodigo()),
                new IdentificacionApartamento(entidad.getIdentificacionApartamento()),
                entidad.getFecha(),
                entidad.getAutor(),
                entidad.getDescripcion(),
                GravedadNovedad.valueOf(entidad.getGravedad()));
        if (entidad.isResuelta()) {
            novedad.resolver(entidad.getFechaResolucion());
        }
        return novedad;
    }

    public PoliticaCancelacionJpaEntity aEntidad(PoliticaCancelacion politica) {
        PoliticaCancelacionJpaEntity entidad = new PoliticaCancelacionJpaEntity();
        entidad.setCodigo(politica.getCodigo().valor());
        entidad.setVersion(politica.getVersion().numero());
        entidad.setVigente(politica.estaVigente());
        entidad.setRetencionNoShow(politica.getRetencionNoShow().valor());
        entidad.setCreadaEn(politica.getCreadaEn());
        entidad.setTramos(politica.getTramos().stream()
                .map(t -> new TramoEmbeddable(t.diasAntelacionMinima(), t.retencion().valor()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        return entidad;
    }

    public PoliticaCancelacion aDominio(PoliticaCancelacionJpaEntity entidad) {
        List<TramoCancelacion> tramos = entidad.getTramos().stream()
                .map(t -> new TramoCancelacion(t.getDiasAntelacionMinima(), new Porcentaje(t.getRetencion())))
                .toList();
        return PoliticaCancelacion.rehidratar(
                new CodigoPolitica(entidad.getCodigo()),
                new VersionPolitica(entidad.getVersion()),
                tramos,
                new Porcentaje(entidad.getRetencionNoShow()),
                entidad.getCreadaEn(),
                entidad.isVigente());
    }

    public RecordatorioJpaEntity aEntidad(Recordatorio recordatorio) {
        RecordatorioJpaEntity entidad = new RecordatorioJpaEntity();
        entidad.setCodigo(recordatorio.getCodigo().valor());
        entidad.setCodigoReserva(recordatorio.getReserva().valor());
        entidad.setTipo(recordatorio.getTipo().name());
        entidad.setMomentoEvento(recordatorio.getMomentoEvento());
        entidad.setFechaRecordatorio(recordatorio.getFechaRecordatorio());
        entidad.setEstado(recordatorio.getEstado().name());
        entidad.setUltimoError(recordatorio.getUltimoError());
        return entidad;
    }

    public Recordatorio aDominio(RecordatorioJpaEntity entidad) {
        return Recordatorio.rehidratar(
                new CodigoRecordatorio(entidad.getCodigo()),
                new CodigoReserva(entidad.getCodigoReserva()),
                TipoRecordatorio.valueOf(entidad.getTipo()),
                entidad.getMomentoEvento(),
                entidad.getFechaRecordatorio(),
                EstadoRecordatorio.valueOf(entidad.getEstado()),
                entidad.getUltimoError());
    }

    private static DineroEmbeddable dinero(Dinero dinero) {
        return new DineroEmbeddable(dinero.valor());
    }

    private static EstanciaEmbeddable estancia(Estancia estancia) {
        return new EstanciaEmbeddable(estancia.entrada(), estancia.salida());
    }

    private static Estancia aEstancia(EstanciaEmbeddable estancia) {
        return new Estancia(estancia.getFechaEntrada(), estancia.getFechaSalida());
    }
}
