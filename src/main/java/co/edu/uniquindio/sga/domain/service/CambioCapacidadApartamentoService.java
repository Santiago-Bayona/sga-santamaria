package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.Capacidad;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

import java.util.List;

/**
 * 7.3: cambiar la capacidad no afecta las reservas ya creadas aunque queden por encima; el sistema advierte
 * al administrador y las lista. Listarlas exige consultar reservas, que el Apartamento no ve.
 */
public class CambioCapacidadApartamentoService {

    private final ApartamentoRepository apartamentoRepository;
    private final ReservaRepository reservaRepository;

    public CambioCapacidadApartamentoService(ApartamentoRepository apartamentoRepository,
                                             ReservaRepository reservaRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.reservaRepository = reservaRepository;
    }

    /** @return reservas activas que quedaron por encima de la nueva capacidad (vacía si no hay advertencia) */
    public List<Reserva> cambiarCapacidad(IdentificacionApartamento identificacion, Capacidad nueva) {
        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(identificacion)
                .orElseThrow(() -> new ReglaDominioException("No existe el apartamento " + identificacion.valor()));
        boolean disminuyo = apartamento.cambiarCapacidad(nueva);
        apartamentoRepository.guardar(apartamento);
        if (!disminuyo) {
            return List.of();
        }
        return reservaRepository.buscarActivasPorApartamento(identificacion).stream()
                .filter(reserva -> !nueva.admite(reserva.totalOcupantes()))
                .toList();
    }
}
