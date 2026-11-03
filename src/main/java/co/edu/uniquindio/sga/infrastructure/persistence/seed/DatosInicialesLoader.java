package co.edu.uniquindio.sga.infrastructure.persistence.seed;

import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.valueobject.Capacidad;
import co.edu.uniquindio.sga.domain.valueobject.CodigoAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.CodigoPolitica;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.infrastructure.config.FichaProperties;
import co.edu.uniquindio.sga.domain.valueobject.GaleriaImagenes;
import co.edu.uniquindio.sga.domain.valueobject.IdTemporada;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoAnual;
import co.edu.uniquindio.sga.domain.valueobject.Mascotas;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.ServicioAdicional;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;
import co.edu.uniquindio.sga.domain.valueobject.TramoCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.Ubicacion;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.util.List;

/**
 * Semilla obligatoria: alojamiento singleton (F-13), temporadas (1 base + 2 especiales) y política vigente (F-07).
 */
@Component
@Order(1)
public class DatosInicialesLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DatosInicialesLoader.class);

    private final AlojamientoRepository alojamientoRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final PoliticaCancelacionRepository politicaRepository;
    private final Clock clock;
    private final FichaProperties ficha;

    public DatosInicialesLoader(AlojamientoRepository alojamientoRepository,
                                ApartamentoRepository apartamentoRepository,
                                PoliticaCancelacionRepository politicaRepository,
                                Clock clock,
                                FichaProperties ficha) {
        this.alojamientoRepository = alojamientoRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.politicaRepository = politicaRepository;
        this.clock = clock;
        this.ficha = ficha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (alojamientoRepositoryYaExiste()) {
            LOG.info("Semilla omitida: el alojamiento Santamaría ya está persistido.");
            return;
        }
        LocalDateTime ahora = LocalDateTime.now(clock);
        sembrarAlojamiento();
        sembrarPolitica(ahora);
        sembrarApartamentos(ahora);
        LOG.info("Semilla de infraestructura cargada: alojamiento, política y 2 apartamentos activos.");
    }

    /**
     * No envuelve esta lectura en la transacción del sembrado.
     * Si el alojamiento no existe, el adaptador lanza y Spring marcaría esa transacción para rollback.
     */
    private boolean alojamientoRepositoryYaExiste() {
        try {
            alojamientoRepository.obtener();
            return true;
        } catch (EntidadNoEncontradaException e) {
            return false;
        }
    }

    private void sembrarAlojamiento() {
        Temporada base = Temporada.base(new IdTemporada("BASE"), "Temporada base");
        Temporada alta = Temporada.especial(new IdTemporada("ALTA"), "Temporada alta",
                List.of(new PeriodoAnual(MonthDay.of(12, 15), MonthDay.of(1, 15))));
        Temporada mitad = Temporada.especial(new IdTemporada("MITAD"), "Mitad de año",
                List.of(new PeriodoAnual(MonthDay.of(6, 15), MonthDay.of(7, 31))));

        Alojamiento alojamiento = new Alojamiento(
                new CodigoAlojamiento("SANTAMARIA"),
                "Santamaría",
                "Alojamiento turístico de Montenegro, Quindío, con apartamentos independientes, piscina y entorno natural.",
                new Ubicacion("Montenegro", "Vereda El Caimo, Montenegro, Quindío",
                        new BigDecimal("4.5650000"), new BigDecimal("-75.7510000")),
                ficha.configuracion(),
                List.of(base, alta, mitad));
        alojamiento.definirNormasConvivencia(List.of(
                "Respetar el horario de silencio a partir de las 22:00.",
                "No se permiten fiestas ni música a alto volumen.",
                "Las mascotas deben permanecer en las áreas comunes con correa."));
        // L-16: el cargo por mascotas ($30.000 por estancia) sale de este servicio adicional configurado
        alojamiento.agregarServicioAdicional(new ServicioAdicional(Mascotas.SERVICIO_ADICIONAL,
                new Dinero(new BigDecimal("30000")), true));
        alojamientoRepository.guardar(alojamiento);
    }

    private void sembrarPolitica(LocalDateTime ahora) {
        PoliticaCancelacion politica = new PoliticaCancelacion(
                new CodigoPolitica("POL-SGA"),
                new VersionPolitica(1),
                List.of(
                        new TramoCancelacion(15, new Porcentaje(0)),
                        new TramoCancelacion(7, new Porcentaje(50)),
                        new TramoCancelacion(0, new Porcentaje(100))),
                new Porcentaje(100),
                ahora);
        politicaRepository.guardar(politica);
    }

    private void sembrarApartamentos(LocalDateTime ahora) {
        crearApartamento("APT-101", "Apartamento Gardenia",
                "Apartamento de un dormitorio con vista al jardín, ideal para parejas.",
                1, 2, 180_000, 250_000, 200_000, ahora);
        crearApartamento("APT-102", "Apartamento Heliconia",
                "Apartamento familiar de dos dormitorios, cerca de la piscina.",
                2, 4, 220_000, 300_000, 250_000, ahora);
    }

    private void crearApartamento(String id, String nombre, String descripcion, int dormitorios, int capacidad,
                                  long tarifaBase, long tarifaAlta, long tarifaMitad, LocalDateTime ahora) {
        Apartamento apartamento = new Apartamento(
                new IdentificacionApartamento(id),
                nombre,
                descripcion,
                dormitorios,
                new Capacidad(capacidad),
                new GaleriaImagenes(List.of(
                        "https://sga.local/galeria/" + id.toLowerCase() + "-1.jpg",
                        "https://sga.local/galeria/" + id.toLowerCase() + "-2.jpg"), 0));
        apartamento.definirDotacion(List.of("WiFi", "Cocina equipada", "Aire acondicionado", "Ropa de cama"));
        apartamento.definirTarifa(new Tarifa(new IdTemporada("BASE"), Dinero.pesos(tarifaBase)), ahora);
        apartamento.definirTarifa(new Tarifa(new IdTemporada("ALTA"), Dinero.pesos(tarifaAlta)), ahora);
        apartamento.definirTarifa(new Tarifa(new IdTemporada("MITAD"), Dinero.pesos(tarifaMitad)), ahora);
        apartamento.activar(List.of(new IdTemporada("BASE"), new IdTemporada("ALTA"), new IdTemporada("MITAD")));
        apartamentoRepository.guardar(apartamento);
    }
}
