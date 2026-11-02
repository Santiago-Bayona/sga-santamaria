package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.EstadoOperativo;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

import java.util.List;

/**
 * Estado operativo (3.3, 7.6). Las transiciones de preparación son de un solo agregado (Apartamento) y podrían
 * ir directo en el caso de uso. Lo que sí cruza agregados es declarar FUERA_DE_SERVICIO: no cambia la
 * disponibilidad por sí solo (3.3), así que se devuelven las reservas activas afectadas para que el
 * administrador registre el bloqueo o reubique. Quién puede hacer cada acción (rol) lo controla el caso de uso.
 */
public class PreparacionApartamentoService {

    private final ApartamentoRepository apartamentoRepository;
    private final ReservaRepository reservaRepository;

    public PreparacionApartamentoService(ApartamentoRepository apartamentoRepository,
                                         ReservaRepository reservaRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.reservaRepository = reservaRepository;
    }

    /** 7.1 personal de servicio: apartamentos que esperan preparación. */
    public List<Apartamento> pendientesDePreparacion() {
        return apartamentoRepository.buscarPorEstadoOperativo(EstadoOperativo.PENDIENTE_PREPARACION);
    }

    /** PENDIENTE_PREPARACION → EN_PREPARACION */
    public void iniciarPreparacion(IdentificacionApartamento identificacion) {
        Apartamento apartamento = apartamento(identificacion);
        apartamento.marcarEnPreparacion();
        apartamentoRepository.guardar(apartamento);
    }

    /** EN_PREPARACION → PREPARADO */
    public void finalizarPreparacion(IdentificacionApartamento identificacion) {
        Apartamento apartamento = apartamento(identificacion);
        apartamento.marcarPreparado();
        apartamentoRepository.guardar(apartamento);
    }

    /**
     * Solo administrador. @return reservas activas del apartamento, que siguen vendidas aunque esté fuera de
     * servicio mientras no exista un bloqueo.
     */
    public List<Reserva> declararFueraDeServicio(IdentificacionApartamento identificacion) {
        Apartamento apartamento = apartamento(identificacion);
        apartamento.declararFueraDeServicio();
        apartamentoRepository.guardar(apartamento);
        return reservaRepository.buscarActivasPorApartamento(identificacion);
    }

    /** FUERA_DE_SERVICIO → PREPARADO */
    public void reintegrarAlServicio(IdentificacionApartamento identificacion) {
        Apartamento apartamento = apartamento(identificacion);
        apartamento.marcarPreparado();
        apartamentoRepository.guardar(apartamento);
    }

    private Apartamento apartamento(IdentificacionApartamento identificacion) {
        return apartamentoRepository.obtenerPorIdentificacion(identificacion)
                .orElseThrow(() -> new ReglaDominioException("No existe el apartamento " + identificacion.valor()));
    }
}
