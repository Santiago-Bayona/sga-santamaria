package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.Cargo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Cotizacion;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Mascotas;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import co.edu.uniquindio.sga.domain.valueobject.ServicioAdicional;
import co.edu.uniquindio.sga.domain.valueobject.TipoCargo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * RN-14: toda modificación repite las seis validaciones de creación, recalcula el valor con las tarifas vigentes
 * y registra la diferencia como ajuste en el folio. RN-22: la política congelada no se toca.
 */
public class ModificacionReservaService {

    private final ReservaRepository reservaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final FolioRepository folioRepository;
    private final DisponibilidadApartamentoService disponibilidad;
    private final CotizadorEstanciaService cotizador;
    private final GestionRecordatoriosService recordatorios;

    public ModificacionReservaService(ReservaRepository reservaRepository, ApartamentoRepository apartamentoRepository,
                                      AlojamientoRepository alojamientoRepository, FolioRepository folioRepository,
                                      DisponibilidadApartamentoService disponibilidad,
                                      CotizadorEstanciaService cotizador, GestionRecordatoriosService recordatorios) {
        this.reservaRepository = reservaRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.folioRepository = folioRepository;
        this.disponibilidad = disponibilidad;
        this.cotizador = cotizador;
        this.recordatorios = recordatorios;
    }

    /**
     * @param nuevosAcompanantes ocupantes SIN el titular (el titular no cambia)
     * @return diferencia registrada como ajuste: positiva (debe más) o negativa (a favor)
     */
    public BigDecimal modificar(CodigoReserva codigo, IdentificacionApartamento nuevoApartamento,
                                Estancia nuevaEstancia, List<Ocupante> nuevosAcompanantes, LocalDateTime ahora) {
        return modificar(codigo, nuevoApartamento, nuevaEstancia, nuevosAcompanantes, null, ahora);
    }

    /**
     * @param nuevasMascotas mascotas de la nueva estancia (L-16, máximo 2); null conserva las que ya tenía la reserva.
     *                       Si pasa de no tener a tener, se carga el servicio adicional "Mascotas"; si pasa de tener a
     *                       no tener, se revierte ese cargo (RN-16).
     */
    public BigDecimal modificar(CodigoReserva codigo, IdentificacionApartamento nuevoApartamento,
                                Estancia nuevaEstancia, List<Ocupante> nuevosAcompanantes, Mascotas nuevasMascotas,
                                LocalDateTime ahora) {
        if (nuevoApartamento == null || nuevaEstancia == null || ahora == null) {
            throw new ReglaDominioException("La modificación requiere apartamento, estancia y fecha actual.");
        }
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReglaDominioException("No existe la reserva " + codigo.valor()));
        // 7.5: solo mientras esté PENDIENTE o CONFIRMADA y no haya iniciado
        if (reserva.getEstado() != EstadoReserva.PENDIENTE && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ReglaDominioException("Solo se puede modificar una reserva PENDIENTE o CONFIRMADA.");
        }
        Folio folio = folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new ReglaDominioException("La reserva " + codigo.valor() + " no tiene folio."));
        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(nuevoApartamento)
                .orElseThrow(() -> new ReglaDominioException("No existe el apartamento " + nuevoApartamento.valor()));
        Alojamiento alojamiento = alojamientoRepository.obtener();

        Mascotas mascotas = nuevasMascotas == null ? reserva.getMascotas() : nuevasMascotas;
        boolean teniaMascotas = reserva.getMascotas().hay();
        ServicioAdicional servicioMascotas = mascotas.hay() && !teniaMascotas
                ? ValidacionesReserva.servicioMascotas(alojamiento) : null;

        // El titular es siempre el primer ocupante del grupo
        List<Ocupante> grupo = ValidacionesReserva.componerGrupo(reserva.getOcupantes().get(0), nuevosAcompanantes);

        // RN-14: las seis validaciones de creación, excluyendo la propia reserva del cálculo de solapamiento
        ValidacionesReserva.validarCondicionesDeVenta(apartamento, alojamiento, nuevaEstancia, grupo.size(),
                ahora.toLocalDate());
        disponibilidad.verificarDisponible(apartamento, nuevaEstancia, grupo.size(), codigo);

        // Recalcula con las tarifas vigentes HOY, no con las de la creación
        Cotizacion nuevaCotizacion = cotizador.cotizar(apartamento, alojamiento, nuevaEstancia, grupo);
        BigDecimal diferencia = reserva.modificar(nuevoApartamento, apartamento.getCapacidad(), nuevaEstancia,
                nuevosAcompanantes, mascotas, nuevaCotizacion, alojamiento.getConfiguracion().umbralEdadFacturable(),
                ahora);

        boolean folioCambio = false;
        if (diferencia.signum() != 0) {
            folio.registrarAjuste(diferencia, ahora.toLocalDate());
            folioCambio = true;
        }
        if (servicioMascotas != null && servicioMascotas.generaCargo()) {
            folio.agregarCargo(TipoCargo.SERVICIO_ADICIONAL, servicioMascotas.nombre(), servicioMascotas.costo(),
                    ahora.toLocalDate());
            folioCambio = true;
        } else if (!mascotas.hay() && teniaMascotas) {
            // RN-16: el cargo por mascotas no se borra, se revierte
            for (Cargo cargo : folio.getCargos()) {
                if (cargo.tipo() == TipoCargo.SERVICIO_ADICIONAL && !cargo.esReverso()
                        && cargo.concepto().equalsIgnoreCase(Mascotas.SERVICIO_ADICIONAL)
                        && !folio.cargoRevertido(cargo.numero())) {
                    folio.revertirCargo(cargo.numero(), ahora.toLocalDate());
                    folioCambio = true;
                }
            }
        }
        if (folioCambio) {
            folioRepository.guardar(folio);
        }
        reservaRepository.guardar(reserva);
        recordatorios.reprogramarPorModificacion(reserva);
        return diferencia;
    }
}
