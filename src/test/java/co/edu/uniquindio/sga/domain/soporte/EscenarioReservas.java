package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.repository.GeneradorCodigoReserva;
import co.edu.uniquindio.sga.domain.service.*;
import co.edu.uniquindio.sga.domain.valueobject.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Escenario completo con servicios reales del dominio y repositorios en memoria:
 * anticipo 50 %, política v1 (>=15 días: 0 %, >=7 días: 50 %, <7 días: 100 %; no-show 100 %).
 */
public class EscenarioReservas {

    public static final IdentificacionApartamento APTO = new IdentificacionApartamento("APT-101");
    public static final LocalDate ENTRADA = LocalDate.of(2026, 9, 10);

    public final Apartamento apartamento = DatosDePrueba.apartamento("APT-101", 4);
    public final ReservaRepositoryEnMemoria reservas = new ReservaRepositoryEnMemoria();
    public final FolioRepositoryEnMemoria folios = new FolioRepositoryEnMemoria();
    public final PoliticaCancelacionRepositoryEnMemoria politicas = new PoliticaCancelacionRepositoryEnMemoria();
    public final AlojamientoRepositoryEnMemoria alojamientos;
    public final CreacionReservaService creacion;
    public final CancelacionReservaService cancelacion;
    public final DeclaracionNoShowService noShow;
    public final LiquidacionPagoService pagos;

    public EscenarioReservas() {
        ConfiguracionAlojamiento base = DatosDePrueba.configuracion();
        ConfiguracionAlojamiento config = new ConfiguracionAlojamiento(base.horaEntrada(), base.horaSalida(),
                base.tiempoPreparacion(), base.plazoConfirmacion(), base.horaLimiteNoShow(),
                base.umbralEdadFacturable(), new Porcentaje(50), Duration.ofHours(24), Duration.ofHours(12));
        alojamientos = new AlojamientoRepositoryEnMemoria(DatosDePrueba.alojamiento(config));
        politicas.con(politicaV1());
        ApartamentoRepositoryEnMemoria apartamentos = new ApartamentoRepositoryEnMemoria().con(apartamento);
        DisponibilidadApartamentoService disponibilidad = new DisponibilidadApartamentoService(reservas,
                new BloqueoRepositoryEnMemoria(), apartamentos, alojamientos);
        CotizadorEstanciaService cotizador = new CotizadorEstanciaService(apartamentos, alojamientos);
        int[] consecutivo = {0};
        GeneradorCodigoReserva generador = () -> new CodigoReserva("RES-" + (++consecutivo[0]));
        creacion = new CreacionReservaService(reservas, apartamentos, alojamientos, politicas, folios, generador,
                disponibilidad, cotizador);
        GestionRecordatoriosService recordatorios = new GestionRecordatoriosService(
                new RecordatorioRepositoryEnMemoria(), reservas, alojamientos);
        cancelacion = new CancelacionReservaService(reservas, folios, politicas, alojamientos, recordatorios);
        noShow = new DeclaracionNoShowService(reservas, folios, politicas, alojamientos, recordatorios);
        pagos = new LiquidacionPagoService(folios, reservas, alojamientos);
    }

    public static PoliticaCancelacion politicaV1() {
        return new PoliticaCancelacion(new CodigoPolitica("POL-1"), new VersionPolitica(1),
                List.of(new TramoCancelacion(15, Porcentaje.CERO), new TramoCancelacion(7, new Porcentaje(50)),
                        new TramoCancelacion(0, Porcentaje.CIEN)),
                Porcentaje.CIEN, DatosDePrueba.AHORA);
    }

    public Estancia estancia() {
        return DatosDePrueba.estancia(ENTRADA, 3);
    }

    public Dinero anticipoDe(co.edu.uniquindio.sga.domain.entity.Reserva reserva) {
        return new Porcentaje(50).aplicarA(reserva.getValorCongelado());
    }

    /** Crea una reserva del portal con el servicio real (abre su folio y congela la política vigente). */
    public co.edu.uniquindio.sga.domain.entity.Reserva crearReserva() {
        return creacion.crear(DatosDePrueba.titular(), APTO, estancia(), List.of(), LocalTime.of(15, 0),
                CanalOrigen.PORTAL, null, DatosDePrueba.AHORA);
    }
}
