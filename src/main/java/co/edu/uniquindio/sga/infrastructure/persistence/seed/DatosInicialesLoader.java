package co.edu.uniquindio.sga.infrastructure.persistence.seed;

import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.service.CotizadorEstanciaService;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.Capacidad;
import co.edu.uniquindio.sga.domain.valueobject.CodigoAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoFolio;
import co.edu.uniquindio.sga.domain.valueobject.CodigoPolitica;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.domain.valueobject.Cotizacion;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.GaleriaImagenes;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.Mascotas;
import co.edu.uniquindio.sga.domain.valueobject.MedioPago;
import co.edu.uniquindio.sga.domain.valueobject.MotivoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.MotivoCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoAnual;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.Registro;
import co.edu.uniquindio.sga.domain.valueobject.Salida;
import co.edu.uniquindio.sga.domain.valueobject.ServicioAdicional;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;
import co.edu.uniquindio.sga.domain.valueobject.TramoCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.Ubicacion;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;
import co.edu.uniquindio.sga.infrastructure.config.FichaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Datos de prueba (12.3). La Ficha (alojamiento, temporadas, tarifas, política, servicios y apartamentos) se lee
 * de {@code sga.ficha} en application.yml: aquí no hay valores del negocio quemados. Además siembra los datos de
 * demostración: titulares, una reserva en cada estado del ciclo de vida, una por cada canal, un bloqueo vigente y
 * un apartamento FUERA_DE_SERVICIO. Las fechas de las reservas son relativas a hoy para que la demo funcione
 * cualquier día.
 */
@Component
@Order(1)
public class DatosInicialesLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DatosInicialesLoader.class);

    private final AlojamientoRepository alojamientoRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final PoliticaCancelacionRepository politicaRepository;
    private final TitularRepository titularRepository;
    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final BloqueoRepository bloqueoRepository;
    private final CotizadorEstanciaService cotizador;
    private final Clock clock;
    private final FichaProperties ficha;

    public DatosInicialesLoader(AlojamientoRepository alojamientoRepository,
                                ApartamentoRepository apartamentoRepository,
                                PoliticaCancelacionRepository politicaRepository,
                                TitularRepository titularRepository,
                                ReservaRepository reservaRepository,
                                FolioRepository folioRepository,
                                BloqueoRepository bloqueoRepository,
                                CotizadorEstanciaService cotizador,
                                Clock clock,
                                FichaProperties ficha) {
        this.alojamientoRepository = alojamientoRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.politicaRepository = politicaRepository;
        this.titularRepository = titularRepository;
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.bloqueoRepository = bloqueoRepository;
        this.cotizador = cotizador;
        this.clock = clock;
        this.ficha = ficha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (alojamientoYaExiste()) {
            LOG.info("Semilla omitida: el alojamiento ya está persistido.");
            return;
        }
        LocalDateTime ahora = LocalDateTime.now(clock);
        Alojamiento alojamiento = sembrarAlojamiento();
        PoliticaCancelacion politica = sembrarPolitica(ahora);
        Map<String, Apartamento> apartamentos = construirApartamentos(alojamiento, ahora);
        Map<String, Titular> titulares = sembrarTitulares();
        sembrarReservas(alojamiento, politica, apartamentos, titulares, ahora);
        sembrarBloqueoYFueraDeServicio(apartamentos, ahora);
        apartamentos.values().forEach(apartamentoRepository::guardar);
        LOG.info("Semilla cargada: {} apartamentos, {} titulares, reservas en los 6 estados, 1 bloqueo.",
                apartamentos.size(), titulares.size());
    }

    /** No envuelve la lectura en la transacción del sembrado (el adaptador lanza si no existe). */
    private boolean alojamientoYaExiste() {
        try {
            alojamientoRepository.obtener();
            return true;
        } catch (EntidadNoEncontradaException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ Ficha (desde application.yml)

    private Alojamiento sembrarAlojamiento() {
        List<Temporada> temporadas = ficha.temporadas().stream()
                .map(t -> t.base()
                        ? Temporada.base(idTemporada(t.id()), t.nombre())
                        : Temporada.especial(idTemporada(t.id()), t.nombre(),
                        t.periodos().stream().map(DatosInicialesLoader::periodo).toList()))
                .toList();

        FichaProperties.Identidad id = ficha.identidad();
        Alojamiento alojamiento = new Alojamiento(
                new CodigoAlojamiento(id.codigo()),
                id.nombre(),
                id.descripcion(),
                new Ubicacion(id.ciudad(), id.direccion(), id.latitud(), id.longitud()),
                ficha.configuracion(),
                temporadas);
        alojamiento.definirNormasConvivencia(ficha.normas());
        ficha.servicios().forEach(s -> alojamiento.agregarServicioAdicional(
                new ServicioAdicional(s.nombre(), Dinero.pesos(s.valor()), s.generaCargo())));
        alojamientoRepository.guardar(alojamiento);
        return alojamiento;
    }

    private PoliticaCancelacion sembrarPolitica(LocalDateTime ahora) {
        FichaProperties.Politica p = ficha.politica();
        PoliticaCancelacion politica = new PoliticaCancelacion(
                new CodigoPolitica(p.codigo()),
                new VersionPolitica(1),
                p.tramos().stream()
                        .map(t -> new TramoCancelacion(t.diasAntelacionMinima(), new Porcentaje(t.retencion())))
                        .toList(),
                new Porcentaje(p.retencionNoShow()),
                ahora);
        politicaRepository.guardar(politica);
        return politica;
    }

    /** Se guardan al final, después de aplicar los estados operativos de la demo. */
    private Map<String, Apartamento> construirApartamentos(Alojamiento alojamiento, LocalDateTime ahora) {
        List<IdTemporada> idsTemporadas = ficha.temporadas().stream().map(t -> idTemporada(t.id())).toList();
        Map<String, Apartamento> apartamentos = new LinkedHashMap<>();
        for (FichaProperties.ApartamentoFicha a : ficha.apartamentos()) {
            String slug = a.id().toLowerCase(Locale.ROOT);
            Apartamento apartamento = new Apartamento(
                    new IdentificacionApartamento(a.id()),
                    a.nombre(),
                    a.descripcion(),
                    a.dormitorios(),
                    new Capacidad(a.capacidad()),
                    new GaleriaImagenes(List.of(
                            "https://sga.local/galeria/" + slug + "-1.jpg",
                            "https://sga.local/galeria/" + slug + "-2.jpg"), 0));
            apartamento.definirDotacion(a.dotacion());
            a.tarifas().forEach((temporada, valor) ->
                    apartamento.definirTarifa(new Tarifa(idTemporada(temporada), Dinero.pesos(valor)), ahora));
            apartamento.activar(idsTemporadas);
            apartamentos.put(a.id().toUpperCase(Locale.ROOT), apartamento);
        }
        return apartamentos;
    }

    // ------------------------------------------------------------------ Datos de demostración (12.3)

    private Map<String, Titular> sembrarTitulares() {
        Map<String, Titular> titulares = new LinkedHashMap<>();
        // TIT-001 es el titular del usuario "huesped" (UsuariosInicialesLoader)
        titulares.put("TIT-001", new Titular(new CodigoTitular("TIT-001"), "Huésped demo",
                LocalDate.of(1995, 3, 14), "3001112233", "huesped@santamaria.co"));
        titulares.put("TIT-002", new Titular(new CodigoTitular("TIT-002"), "Carlos Restrepo",
                LocalDate.of(1988, 7, 2), "3104445566", "carlos.restrepo@correo.co"));
        titulares.put("TIT-003", new Titular(new CodigoTitular("TIT-003"), "Marcela Giraldo",
                LocalDate.of(1990, 11, 20), "3157778899", "marcela.giraldo@correo.co"));
        titulares.values().forEach(titularRepository::guardar);
        return titulares;
    }

    /**
     * Una reserva por estado (sección 8) y al menos una por canal (2.4), sin solapes entre ellas.
     * Las futuras se crean con {@code Reserva.crear} (pasan por las reglas del dominio); las que ya ocurrieron
     * (EN_CURSO, FINALIZADA, NO_SHOW) se cargan como históricas con {@code rehidratar}, porque RN-04 impide
     * crear reservas con entrada pasada.
     */
    private void sembrarReservas(Alojamiento alojamiento, PoliticaCancelacion politica,
                                 Map<String, Apartamento> apts, Map<String, Titular> tits, LocalDateTime ahora) {
        LocalDate hoy = ahora.toLocalDate();
        LocalTime llegada = ficha.horaEntrada();
        List<Ocupante> sinAcompanantes = List.of();
        List<Ocupante> conNino = List.of(new Ocupante("Sofía Restrepo", hoy.minusYears(2)));

        // PENDIENTE — portal (huésped demo)
        Reserva pendiente = nueva("RES-DEMO-001", tits.get("TIT-001"), apts.get("APT-01"),
                new Estancia(hoy.plusDays(10), hoy.plusDays(13)), sinAcompanantes, CanalOrigen.PORTAL, null,
                alojamiento, politica, llegada, ahora);
        guardarConFolio(pendiente, ahora, false);

        // CONFIRMADA — directo, con un menor no facturable
        Reserva confirmada = nueva("RES-DEMO-002", tits.get("TIT-002"), apts.get("APT-02"),
                new Estancia(hoy.plusDays(5), hoy.plusDays(7)), conNino, CanalOrigen.DIRECTO, null,
                alojamiento, politica, llegada, ahora);
        confirmada.confirmar();
        guardarConFolio(confirmada, ahora, false);

        // CANCELADA — directo
        Reserva cancelada = nueva("RES-DEMO-003", tits.get("TIT-003"), apts.get("APT-03"),
                new Estancia(hoy.plusDays(20), hoy.plusDays(22)), sinAcompanantes, CanalOrigen.DIRECTO, null,
                alojamiento, politica, llegada, ahora);
        cancelada.cancelar(new MotivoCancelacion("Cambio de planes del titular"));
        guardarConFolio(cancelada, ahora, false);

        // EN_CURSO — directo; el grupo llegó ayer y ocupa el apartamento
        Apartamento colibri = apts.get("APT-05");
        Reserva enCurso = historica("RES-DEMO-004", tits.get("TIT-003"), colibri,
                new Estancia(hoy.minusDays(1), hoy.plusDays(2)), CanalOrigen.DIRECTO, null,
                EstadoReserva.EN_CURSO, hoy.minusDays(1).atTime(llegada), null,
                alojamiento, politica, ahora.minusDays(8));
        colibri.marcarOcupado();
        guardarConFolio(enCurso, ahora.minusDays(8), false);

        // FINALIZADA — externo; pagada por completo
        Reserva finalizada = historica("RES-DEMO-005", tits.get("TIT-002"), apts.get("APT-01"),
                new Estancia(hoy.minusDays(6), hoy.minusDays(3)), CanalOrigen.EXTERNO,
                new IdentificadorExterno("EXT-0001"), EstadoReserva.FINALIZADA,
                hoy.minusDays(6).atTime(llegada), hoy.minusDays(3).atTime(ficha.horaSalida()),
                alojamiento, politica, ahora.minusDays(20));
        guardarConFolio(finalizada, ahora.minusDays(20), true);

        // NO_SHOW — portal
        Reserva noShow = historica("RES-DEMO-006", tits.get("TIT-001"), apts.get("APT-02"),
                new Estancia(hoy.minusDays(4), hoy.minusDays(2)), CanalOrigen.PORTAL, null,
                EstadoReserva.NO_SHOW, null, null, alojamiento, politica, ahora.minusDays(15));
        guardarConFolio(noShow, ahora.minusDays(15), false);
    }

    /** RN-07 y 3.3: el apartamento fuera de servicio queda además bloqueado, para que no se venda a futuro. */
    private void sembrarBloqueoYFueraDeServicio(Map<String, Apartamento> apts, LocalDateTime ahora) {
        LocalDate hoy = ahora.toLocalDate();
        Apartamento palma = apts.get("APT-04");
        palma.declararFueraDeServicio();
        bloqueoRepository.guardar(new Bloqueo(new CodigoBloqueo("BLQ-DEMO-001"), palma.getIdentificacion(),
                new Estancia(hoy, hoy.plusDays(15)), new MotivoBloqueo("Reparación de plomería"), ahora));
    }

    // ------------------------------------------------------------------ Auxiliares

    private Reserva nueva(String codigo, Titular titular, Apartamento apartamento, Estancia estancia,
                          List<Ocupante> acompanantes, CanalOrigen canal, IdentificadorExterno externo,
                          Alojamiento alojamiento, PoliticaCancelacion politica, LocalTime llegada,
                          LocalDateTime ahora) {
        Cotizacion cotizacion = cotizador.cotizar(apartamento, alojamiento, estancia,
                grupo(titular, acompanantes));
        return Reserva.crear(new CodigoReserva(codigo), titular, apartamento.getIdentificacion(),
                apartamento.getCapacidad(), estancia, acompanantes, Mascotas.NINGUNA, llegada, cotizacion,
                politica.getVersion(), canal, externo, alojamiento.getConfiguracion().umbralEdadFacturable(),
                ahora);
    }

    private Reserva historica(String codigo, Titular titular, Apartamento apartamento, Estancia estancia,
                              CanalOrigen canal, IdentificadorExterno externo, EstadoReserva estado,
                              LocalDateTime registro, LocalDateTime salida, Alojamiento alojamiento,
                              PoliticaCancelacion politica, LocalDateTime creadaEn) {
        List<Ocupante> grupo = grupo(titular, List.of());
        Cotizacion cotizacion = cotizador.cotizar(apartamento, alojamiento, estancia, grupo);
        return Reserva.rehidratar(new CodigoReserva(codigo), titular.getCodigo(), titular.comoOcupante(),
                apartamento.getIdentificacion(), estancia, grupo, ficha.horaEntrada(), cotizacion,
                politica.getVersion(), canal, externo, creadaEn, estado,
                registro == null ? null : new Registro(registro),
                salida == null ? null : new Salida(salida),
                null, Mascotas.NINGUNA);
    }

    private static List<Ocupante> grupo(Titular titular, List<Ocupante> acompanantes) {
        return java.util.stream.Stream.concat(java.util.stream.Stream.of(titular.comoOcupante()),
                acompanantes.stream()).toList();
    }

    /** 7.7: toda reserva tiene su folio con el cargo de alojamiento; la finalizada queda saldada. */
    private void guardarConFolio(Reserva reserva, LocalDateTime creadaEn, boolean saldar) {
        reservaRepository.guardar(reserva);
        Folio folio = Folio.abrir(new CodigoFolio("FOL-" + reserva.getCodigo().valor()), reserva.getCodigo(),
                "Alojamiento: " + reserva.getEstancia().numeroNoches() + " noche(s)", reserva.getValorCongelado(),
                creadaEn.toLocalDate());
        if (saldar) {
            folio.registrarPago(reserva.getValorCongelado(), MedioPago.TRANSFERENCIA,
                    reserva.getEstancia().salida());
            folio.cerrar();
        }
        folioRepository.guardar(folio);
    }

    private static IdTemporada idTemporada(String id) {
        return new IdTemporada(id.trim().toUpperCase(Locale.ROOT));
    }

    /** "MM-dd/MM-dd" → PeriodoAnual. */
    private static PeriodoAnual periodo(String texto) {
        String[] partes = texto.split("/");
        return new PeriodoAnual(MonthDay.parse("--" + partes[0].trim()), MonthDay.parse("--" + partes[1].trim()));
    }
}