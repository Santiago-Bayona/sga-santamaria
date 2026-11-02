package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.GeneradorCodigoReserva;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.CodigoFolio;
import co.edu.uniquindio.sga.domain.valueobject.Cotizacion;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.Mascotas;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import co.edu.uniquindio.sga.domain.valueobject.ServicioAdicional;
import co.edu.uniquindio.sga.domain.valueobject.TipoCargo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 7.5 creación de reserva: las seis validaciones en orden, el congelamiento del valor y de la política (RN-22)
 * y la apertura del folio (7.7). Las validaciones están repartidas entre Apartamento, Alojamiento, Reserva,
 * Bloqueo y las demás reservas, y ninguna reserva puede validarse a sí misma antes de existir.
 */
public class CreacionReservaService {

    private final ReservaRepository reservaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final PoliticaCancelacionRepository politicaRepository;
    private final FolioRepository folioRepository;
    private final GeneradorCodigoReserva generadorCodigo;
    private final DisponibilidadApartamentoService disponibilidad;
    private final CotizadorEstanciaService cotizador;

    public CreacionReservaService(ReservaRepository reservaRepository, ApartamentoRepository apartamentoRepository,
                                  AlojamientoRepository alojamientoRepository,
                                  PoliticaCancelacionRepository politicaRepository, FolioRepository folioRepository,
                                  GeneradorCodigoReserva generadorCodigo,
                                  DisponibilidadApartamentoService disponibilidad, CotizadorEstanciaService cotizador) {
        this.reservaRepository = reservaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.politicaRepository = politicaRepository;
        this.folioRepository = folioRepository;
        this.generadorCodigo = generadorCodigo;
        this.disponibilidad = disponibilidad;
        this.cotizador = cotizador;
    }

    /**
     * @param titular        titular ya existente (con o sin usuario, 3.6)
     * @param acompanantes   ocupantes SIN el titular: la reserva lo incluye siempre
     * @param identificadorExterno solo para canal EXTERNO (RN-19), null en portal y directo
     */
    public Reserva crear(Titular titular, IdentificacionApartamento idApartamento, Estancia estancia,
                         List<Ocupante> acompanantes, LocalTime horaLlegadaEstimada, CanalOrigen canal,
                         IdentificadorExterno identificadorExterno, LocalDateTime ahora) {
        return crear(titular, idApartamento, estancia, acompanantes, Mascotas.NINGUNA, horaLlegadaEstimada, canal,
                identificadorExterno, ahora);
    }

    /**
     * @param mascotas mascotas de la estancia (L-16): máximo 2; si hay, se carga el servicio adicional "Mascotas"
     *                 una sola vez por estancia, con el valor configurado en el alojamiento
     */
    public Reserva crear(Titular titular, IdentificacionApartamento idApartamento, Estancia estancia,
                         List<Ocupante> acompanantes, Mascotas mascotas, LocalTime horaLlegadaEstimada,
                         CanalOrigen canal, IdentificadorExterno identificadorExterno, LocalDateTime ahora) {
        if (mascotas == null) {
            throw new ReglaDominioException("Las mascotas son obligatorias (use Mascotas.NINGUNA si no hay).");
        }
        if (titular == null || idApartamento == null || estancia == null || canal == null || ahora == null) {
            throw new ReglaDominioException("La reserva requiere titular, apartamento, estancia, canal y fecha actual.");
        }
        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(idApartamento)
                .orElseThrow(() -> new ReglaDominioException("No existe el apartamento " + idApartamento.valor()));
        Alojamiento alojamiento = alojamientoRepository.obtener();
        List<Ocupante> grupo = ValidacionesReserva.componerGrupo(titular.comoOcupante(), acompanantes);
        ServicioAdicional servicioMascotas = mascotas.hay() ? ValidacionesReserva.servicioMascotas(alojamiento) : null;

        // 7.5 condiciones 2, 3 y 4
        ValidacionesReserva.validarCondicionesDeVenta(apartamento, alojamiento, estancia, grupo.size(), ahora.toLocalDate());
        // 7.5 condiciones 5 y 6: solapamientos (RN-01, RN-07) y tiempo de preparación (RN-20)
        disponibilidad.verificarDisponible(apartamento, estancia, grupo.size(), null);

        // RN-22: se congelan el valor (con su desglose) y la versión vigente de la política
        Cotizacion cotizacion = cotizador.cotizar(apartamento, alojamiento, estancia, grupo);
        PoliticaCancelacion politicaVigente = politicaRepository.obtenerVigente();

        Reserva reserva = Reserva.crear(generadorCodigo.generar(), titular, idApartamento, apartamento.getCapacidad(),
                estancia, acompanantes, mascotas, horaLlegadaEstimada, cotizacion, politicaVigente.getVersion(), canal,
                identificadorExterno, alojamiento.getConfiguracion().umbralEdadFacturable(), ahora);

        // 7.7: el folio se abre con el cargo de alojamiento, para poder registrar el anticipo antes de la llegada
        Folio folio = Folio.abrir(new CodigoFolio("FOL-" + reserva.getCodigo().valor()), reserva.getCodigo(),
                "Alojamiento: " + estancia.numeroNoches() + " noche(s)", reserva.getValorCongelado(),
                ahora.toLocalDate());
        if (servicioMascotas != null && servicioMascotas.generaCargo()) {
            folio.agregarCargo(TipoCargo.SERVICIO_ADICIONAL, servicioMascotas.nombre(), servicioMascotas.costo(),
                    ahora.toLocalDate());
        }

        reservaRepository.guardar(reserva);
        folioRepository.guardar(folio);
        return reserva;
    }
}