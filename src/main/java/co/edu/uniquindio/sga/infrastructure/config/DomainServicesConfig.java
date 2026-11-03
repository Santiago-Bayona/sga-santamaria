package co.edu.uniquindio.sga.infrastructure.config;

import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.repository.CanalExternoRepository;
import co.edu.uniquindio.sga.domain.repository.ConflictoCanalRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.GeneradorCodigoReserva;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.repository.RecordatorioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.service.CambioCapacidadApartamentoService;
import co.edu.uniquindio.sga.domain.service.CancelacionReservaService;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService;
import co.edu.uniquindio.sga.domain.service.ConfirmacionReservaService;
import co.edu.uniquindio.sga.domain.service.CotizadorEstanciaService;
import co.edu.uniquindio.sga.domain.service.CreacionReservaService;
import co.edu.uniquindio.sga.domain.service.DeclaracionNoShowService;
import co.edu.uniquindio.sga.domain.service.DisponibilidadApartamentoService;
import co.edu.uniquindio.sga.domain.service.EntregaApartamentoService;
import co.edu.uniquindio.sga.domain.service.GestionBloqueoApartamentoService;
import co.edu.uniquindio.sga.domain.service.GestionRecordatoriosService;
import co.edu.uniquindio.sga.domain.service.GestionTemporadasService;
import co.edu.uniquindio.sga.domain.service.IndicadoresService;
import co.edu.uniquindio.sga.domain.service.LiquidacionPagoService;
import co.edu.uniquindio.sga.domain.service.LiquidacionSalidaService;
import co.edu.uniquindio.sga.domain.service.ModificacionReservaService;
import co.edu.uniquindio.sga.domain.service.PreparacionApartamentoService;
import co.edu.uniquindio.sga.domain.service.RetiroApartamentoService;
import co.edu.uniquindio.sga.domain.service.VencimientoReservasPendientesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Materializa los servicios de dominio (sin anotaciones Spring) como beans de infraestructura.
 * Los casos de uso de aplicación ya están anotados con @Service y @Transactional.
 */
@Configuration
public class DomainServicesConfig {

    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public DisponibilidadApartamentoService disponibilidadApartamentoService(
            ReservaRepository reservaRepository, BloqueoRepository bloqueoRepository,
            ApartamentoRepository apartamentoRepository, AlojamientoRepository alojamientoRepository) {
        return new DisponibilidadApartamentoService(reservaRepository, bloqueoRepository,
                apartamentoRepository, alojamientoRepository);
    }

    @Bean
    public CotizadorEstanciaService cotizadorEstanciaService(
            ApartamentoRepository apartamentoRepository, AlojamientoRepository alojamientoRepository) {
        return new CotizadorEstanciaService(apartamentoRepository, alojamientoRepository);
    }

    @Bean
    public GestionRecordatoriosService gestionRecordatoriosService(
            RecordatorioRepository recordatorioRepository, ReservaRepository reservaRepository,
            AlojamientoRepository alojamientoRepository) {
        return new GestionRecordatoriosService(recordatorioRepository, reservaRepository, alojamientoRepository);
    }

    @Bean
    public CreacionReservaService creacionReservaService(
            ReservaRepository reservaRepository, ApartamentoRepository apartamentoRepository,
            AlojamientoRepository alojamientoRepository, PoliticaCancelacionRepository politicaRepository,
            FolioRepository folioRepository, GeneradorCodigoReserva generadorCodigo,
            DisponibilidadApartamentoService disponibilidad, CotizadorEstanciaService cotizador) {
        return new CreacionReservaService(reservaRepository, apartamentoRepository, alojamientoRepository,
                politicaRepository, folioRepository, generadorCodigo, disponibilidad, cotizador);
    }

    @Bean
    public ConfirmacionReservaService confirmacionReservaService(
            ReservaRepository reservaRepository, FolioRepository folioRepository,
            AlojamientoRepository alojamientoRepository, GestionRecordatoriosService recordatorios) {
        return new ConfirmacionReservaService(reservaRepository, folioRepository, alojamientoRepository, recordatorios);
    }

    @Bean
    public ModificacionReservaService modificacionReservaService(
            ReservaRepository reservaRepository, ApartamentoRepository apartamentoRepository,
            AlojamientoRepository alojamientoRepository, FolioRepository folioRepository,
            DisponibilidadApartamentoService disponibilidad, CotizadorEstanciaService cotizador,
            GestionRecordatoriosService recordatorios) {
        return new ModificacionReservaService(reservaRepository, apartamentoRepository, alojamientoRepository,
                folioRepository, disponibilidad, cotizador, recordatorios);
    }

    @Bean
    public CancelacionReservaService cancelacionReservaService(
            ReservaRepository reservaRepository, FolioRepository folioRepository,
            PoliticaCancelacionRepository politicaRepository, AlojamientoRepository alojamientoRepository,
            GestionRecordatoriosService recordatorios) {
        return new CancelacionReservaService(reservaRepository, folioRepository, politicaRepository,
                alojamientoRepository, recordatorios);
    }

    @Bean
    public DeclaracionNoShowService declaracionNoShowService(
            ReservaRepository reservaRepository, FolioRepository folioRepository,
            PoliticaCancelacionRepository politicaRepository, AlojamientoRepository alojamientoRepository,
            GestionRecordatoriosService recordatorios) {
        return new DeclaracionNoShowService(reservaRepository, folioRepository, politicaRepository,
                alojamientoRepository, recordatorios);
    }

    @Bean
    public VencimientoReservasPendientesService vencimientoReservasPendientesService(
            ReservaRepository reservaRepository, FolioRepository folioRepository,
            AlojamientoRepository alojamientoRepository, GestionRecordatoriosService recordatorios) {
        return new VencimientoReservasPendientesService(reservaRepository, folioRepository,
                alojamientoRepository, recordatorios);
    }

    @Bean
    public LiquidacionPagoService liquidacionPagoService(
            FolioRepository folioRepository, ReservaRepository reservaRepository,
            AlojamientoRepository alojamientoRepository) {
        return new LiquidacionPagoService(folioRepository, reservaRepository, alojamientoRepository);
    }

    @Bean
    public LiquidacionSalidaService liquidacionSalidaService(
            ReservaRepository reservaRepository, FolioRepository folioRepository,
            ApartamentoRepository apartamentoRepository, GestionRecordatoriosService recordatorios) {
        return new LiquidacionSalidaService(reservaRepository, folioRepository, apartamentoRepository, recordatorios);
    }

    @Bean
    public EntregaApartamentoService entregaApartamentoService(
            ReservaRepository reservaRepository, ApartamentoRepository apartamentoRepository) {
        return new EntregaApartamentoService(reservaRepository, apartamentoRepository);
    }

    @Bean
    public PreparacionApartamentoService preparacionApartamentoService(
            ApartamentoRepository apartamentoRepository, ReservaRepository reservaRepository) {
        return new PreparacionApartamentoService(apartamentoRepository, reservaRepository);
    }

    @Bean
    public GestionBloqueoApartamentoService gestionBloqueoApartamentoService(
            BloqueoRepository bloqueoRepository, ReservaRepository reservaRepository,
            ApartamentoRepository apartamentoRepository) {
        return new GestionBloqueoApartamentoService(bloqueoRepository, reservaRepository, apartamentoRepository);
    }

    @Bean
    public GestionTemporadasService gestionTemporadasService(
            AlojamientoRepository alojamientoRepository, ApartamentoRepository apartamentoRepository) {
        return new GestionTemporadasService(alojamientoRepository, apartamentoRepository);
    }

    @Bean
    public RetiroApartamentoService retiroApartamentoService(
            ApartamentoRepository apartamentoRepository, ReservaRepository reservaRepository) {
        return new RetiroApartamentoService(apartamentoRepository, reservaRepository);
    }

    @Bean
    public CambioCapacidadApartamentoService cambioCapacidadApartamentoService(
            ApartamentoRepository apartamentoRepository, ReservaRepository reservaRepository) {
        return new CambioCapacidadApartamentoService(apartamentoRepository, reservaRepository);
    }

    @Bean
    public ConciliacionCanalExternoService conciliacionCanalExternoService(
            ReservaRepository reservaRepository, ConflictoCanalRepository conflictoRepository,
            CanalExternoRepository canalRepository, CreacionReservaService creacion,
            CancelacionReservaService cancelacion) {
        return new ConciliacionCanalExternoService(reservaRepository, conflictoRepository, canalRepository,
                creacion, cancelacion);
    }

    /** Reportes e indicadores (7.9). No necesita repositorios: los casos de uso le pasan los datos ya cargados. */
    @Bean
    public IndicadoresService indicadoresService() {
        return new IndicadoresService();
    }
}
