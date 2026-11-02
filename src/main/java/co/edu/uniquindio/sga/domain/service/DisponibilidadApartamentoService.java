package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Disponibilidad;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.MotivoNoDisponibilidad;

import java.util.List;

/**
 * Disponibilidad (3.3): RN-01, RN-02, RN-07 y RN-20. Ningún agregado puede evaluarla solo:
 * una Reserva no ve a las demás reservas ni a los bloqueos del apartamento.
 */
public class DisponibilidadApartamentoService {

    private final ReservaRepository reservaRepository;
    private final BloqueoRepository bloqueoRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final AlojamientoRepository alojamientoRepository;

    public DisponibilidadApartamentoService(ReservaRepository reservaRepository, BloqueoRepository bloqueoRepository,
                                            ApartamentoRepository apartamentoRepository,
                                            AlojamientoRepository alojamientoRepository) {
        this.reservaRepository = reservaRepository;
        this.bloqueoRepository = bloqueoRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.alojamientoRepository = alojamientoRepository;
    }

    /**
     * Calcula la disponibilidad y devuelve TODOS los motivos por los que no se puede vender.
     * @param reservaExcluida la reserva que se está modificando (RN-14), o null al crear.
     */
    public Disponibilidad evaluar(Apartamento apartamento, Estancia estancia, int totalOcupantes,
                                  CodigoReserva reservaExcluida) {
        if (apartamento == null || estancia == null) {
            throw new ReglaDominioException("La disponibilidad requiere apartamento y estancia.");
        }
        Disponibilidad resultado = Disponibilidad.LIBRE;
        if (!apartamento.estaActivo()) {
            resultado = resultado.combinarCon(Disponibilidad.noDisponible(MotivoNoDisponibilidad.APARTAMENTO_INACTIVO));
        }
        // RN-02: todos los ocupantes cuentan, sean facturables o no
        if (!apartamento.admite(totalOcupantes)) {
            resultado = resultado.combinarCon(Disponibilidad.noDisponible(MotivoNoDisponibilidad.CAPACIDAD_INSUFICIENTE));
        }

        boolean rotacionMismoDia = alojamientoRepository.obtener().getConfiguracion()
                .permiteEntradaElMismoDiaDeUnaSalida();
        for (Reserva otra : reservaRepository.buscarActivasPorApartamento(apartamento.getIdentificacion())) {
            if (otra.getCodigo().equals(reservaExcluida)) {
                continue;
            }
            // RN-01: solapamiento según 3.1, sin importar el canal
            if (otra.seSolapaCon(estancia)) {
                resultado = resultado.combinarCon(Disponibilidad.noDisponible(MotivoNoDisponibilidad.SOLAPA_RESERVA_ACTIVA));
            }
            // RN-20: si la preparación no cabe entre salida y entrada, no hay rotación el mismo día (en ningún sentido)
            if (!rotacionMismoDia && hayRotacionMismoDia(otra.getEstancia(), estancia)) {
                resultado = resultado.combinarCon(
                        Disponibilidad.noDisponible(MotivoNoDisponibilidad.TIEMPO_PREPARACION_INSUFICIENTE));
            }
        }

        // RN-07: un bloqueo vigente sobre alguna noche la deja no disponible
        for (Bloqueo bloqueo : bloqueoRepository.buscarVigentesPorApartamento(apartamento.getIdentificacion())) {
            if (bloqueo.seSolapaCon(estancia)) {
                resultado = resultado.combinarCon(Disponibilidad.noDisponible(MotivoNoDisponibilidad.SOLAPA_BLOQUEO));
            }
        }
        return resultado;
    }

    /** Usado por creación y modificación: lanza excepción con los motivos si no está disponible. */
    public void verificarDisponible(Apartamento apartamento, Estancia estancia, int totalOcupantes,
                                    CodigoReserva reservaExcluida) {
        evaluar(apartamento, estancia, totalOcupantes, reservaExcluida).exigirDisponible();
    }

    public boolean estaDisponible(Apartamento apartamento, Estancia estancia, int totalOcupantes) {
        return evaluar(apartamento, estancia, totalOcupantes, null).estaDisponible();
    }

    /** 7.5 búsqueda: apartamentos activos disponibles para el rango y el tamaño del grupo. */
    public List<Apartamento> buscarDisponibles(Estancia estancia, int totalOcupantes) {
        return apartamentoRepository.buscarActivos().stream()
                .filter(apartamento -> estaDisponible(apartamento, estancia, totalOcupantes))
                .toList();
    }

    private static boolean hayRotacionMismoDia(Estancia existente, Estancia solicitada) {
        return existente.salida().equals(solicitada.entrada()) || solicitada.salida().equals(existente.entrada());
    }
}
