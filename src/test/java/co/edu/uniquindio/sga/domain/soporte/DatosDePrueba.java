package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.entity.Temporada;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.service.CotizadorEstanciaService;
import co.edu.uniquindio.sga.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica de objetos de prueba. Construye agregados reales del dominio (sin mocks): las reglas se prueban
 * sobre el código de producción, solo los repositorios (puertos) se reemplazan por versiones en memoria.
 */
public final class DatosDePrueba {

    public static final LocalDateTime AHORA = LocalDateTime.of(2026, 6, 1, 10, 0);
    public static final LocalDate HOY = AHORA.toLocalDate();

    public static final IdTemporada TEMPORADA_BASE = new IdTemporada("BASE");
    public static final IdTemporada TEMPORADA_MEDIA = new IdTemporada("MEDIA");   // 1 jul – 31 jul
    public static final IdTemporada TEMPORADA_ALTA = new IdTemporada("ALTA");     // 15 dic – 10 ene

    public static final Dinero TARIFA_BASE = Dinero.pesos(100_000);
    public static final Dinero TARIFA_MEDIA = Dinero.pesos(150_000);
    public static final Dinero TARIFA_ALTA = Dinero.pesos(200_000);

    public static final UmbralEdadFacturable UMBRAL = new UmbralEdadFacturable(5);
    public static final LocalTime HORA_LLEGADA = LocalTime.of(15, 0);

    private DatosDePrueba() {
    }

    public static ConfiguracionAlojamiento configuracion() {
        return configuracion(Duration.ofHours(2));
    }

    /** Entrada 15:00 y salida 11:00: con preparación de más de 4 h no cabe rotación el mismo día (RN-20). */
    public static ConfiguracionAlojamiento configuracion(Duration tiempoPreparacion) {
        return new ConfiguracionAlojamiento(
                LocalTime.of(15, 0), LocalTime.of(11, 0),
                new TiempoPreparacion(tiempoPreparacion),
                new PlazoConfirmacion(24),
                LocalTime.of(18, 0), UMBRAL, Porcentaje.CERO,
                Duration.ofHours(24), Duration.ofHours(12));
    }

    public static Alojamiento alojamiento() {
        return alojamiento(configuracion());
    }

    public static Alojamiento alojamiento(ConfiguracionAlojamiento configuracion) {
        List<Temporada> temporadas = List.of(
                Temporada.base(TEMPORADA_BASE, "Base"),
                Temporada.especial(TEMPORADA_MEDIA, "Media",
                        List.of(new PeriodoAnual(MonthDay.of(7, 1), MonthDay.of(7, 31)))),
                Temporada.especial(TEMPORADA_ALTA, "Alta",
                        List.of(new PeriodoAnual(MonthDay.of(12, 15), MonthDay.of(1, 10)))));
        return new Alojamiento(new CodigoAlojamiento("ALOJ-1"), "Apartamentos del Parque",
                "Alojamiento de prueba",
                new Ubicacion("Armenia", "Calle 1 # 2-3", new BigDecimal("4.53"), new BigDecimal("-75.68")),
                configuracion, temporadas);
    }

    /** Apartamento ACTIVO con tarifa en las tres temporadas. */
    public static Apartamento apartamento(String identificacion, int capacidad) {
        Apartamento apartamento = apartamentoSinTarifas(identificacion, capacidad);
        apartamento.definirTarifa(new Tarifa(TEMPORADA_BASE, TARIFA_BASE), AHORA);
        apartamento.definirTarifa(new Tarifa(TEMPORADA_MEDIA, TARIFA_MEDIA), AHORA);
        apartamento.definirTarifa(new Tarifa(TEMPORADA_ALTA, TARIFA_ALTA), AHORA);
        apartamento.activar(List.of(TEMPORADA_BASE, TEMPORADA_MEDIA, TEMPORADA_ALTA));
        return apartamento;
    }

    public static Apartamento apartamentoSinTarifas(String identificacion, int capacidad) {
        return new Apartamento(new IdentificacionApartamento(identificacion), "Apartamento " + identificacion,
                "Apartamento de prueba", 2, new Capacidad(capacidad),
                new GaleriaImagenes(List.of("https://imagenes.test/1.jpg"), 0));
    }

    public static Titular titular() {
        return new Titular(new CodigoTitular("TIT-1"), "Ana Gómez", LocalDate.of(1990, 5, 5),
                "3001234567", "ana@correo.com");
    }

    public static Ocupante adulto(String nombre) {
        return new Ocupante(nombre, LocalDate.of(1985, 3, 3));
    }

    /** Ocupante que, a la fecha indicada, tiene exactamente la edad pedida (cumple años ese mismo día). */
    public static Ocupante conEdadEn(String nombre, int anios, LocalDate fecha) {
        return new Ocupante(nombre, fecha.minusYears(anios));
    }

    public static Estancia estancia(LocalDate entrada, int noches) {
        return new Estancia(entrada, entrada.plusDays(noches));
    }

    public static Cotizacion cotizar(Apartamento apartamento, Estancia estancia, List<Ocupante> grupo) {
        CotizadorEstanciaService cotizador =
                new CotizadorEstanciaService(new ApartamentoRepositoryEnMemoria(), new AlojamientoRepositoryEnMemoria(alojamiento()));
        return cotizador.cotizar(apartamento, alojamiento(), estancia, grupo);
    }

    /** Reserva PENDIENTE creada "AHORA" por el portal, con cotización real del dominio. */
    public static Reserva reservaPendiente(String codigo, Apartamento apartamento, Estancia estancia,
                                           List<Ocupante> acompanantes) {
        Titular titular = titular();
        List<Ocupante> grupo = new ArrayList<>();
        grupo.add(titular.comoOcupante());
        grupo.addAll(acompanantes);
        return Reserva.crear(new CodigoReserva(codigo), titular, apartamento.getIdentificacion(),
                apartamento.getCapacidad(), estancia, acompanantes, HORA_LLEGADA,
                cotizar(apartamento, estancia, grupo), new VersionPolitica(1), CanalOrigen.PORTAL, null,
                UMBRAL, AHORA);
    }

    public static Reserva reservaPendiente(String codigo, Apartamento apartamento, Estancia estancia) {
        return reservaPendiente(codigo, apartamento, estancia, List.of());
    }

    public static Reserva reservaConfirmada(String codigo, Apartamento apartamento, Estancia estancia) {
        Reserva reserva = reservaPendiente(codigo, apartamento, estancia);
        reserva.confirmar();
        return reserva;
    }

    public static Reserva reservaEnCurso(String codigo, Apartamento apartamento, Estancia estancia) {
        Reserva reserva = reservaConfirmada(codigo, apartamento, estancia);
        reserva.registrarLlegada(new Registro(estancia.entrada().atTime(16, 0)));
        return reserva;
    }
}
