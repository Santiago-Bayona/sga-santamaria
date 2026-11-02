package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Registro;

import java.time.LocalDateTime;

/**
 * 7.6 registro de llegada: reserva CONFIRMADA y fecha de entrada alcanzada (RN-10, los valida Reserva) y
 * apartamento PREPARADO (RN-11, lo sabe Apartamento). La Reserva no puede leer el estado operativo del
 * Apartamento: el servicio decide SI se entrega, cada agregado decide CÓMO cambia.
 */
public class EntregaApartamentoService {

    private final ReservaRepository reservaRepository;
    private final ApartamentoRepository apartamentoRepository;

    public EntregaApartamentoService(ReservaRepository reservaRepository, ApartamentoRepository apartamentoRepository) {
        this.reservaRepository = reservaRepository;
        this.apartamentoRepository = apartamentoRepository;
    }

    public void registrarLlegada(CodigoReserva codigo, LocalDateTime ahora) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReglaDominioException("No existe la reserva " + codigo.valor()));
        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(reserva.getApartamento())
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe el apartamento " + reserva.getApartamento().valor()));

        // RN-10 primero (orden del 7.6); nada se guarda si algo falla después
        reserva.registrarLlegada(new Registro(ahora));
        // RN-11
        if (!apartamento.puedeRecibirGrupo()) {
            throw new ReglaDominioException("El apartamento no puede recibir al grupo: su estado operativo es "
                    + apartamento.getEstadoOperativo() + " y debe ser PREPARADO.");
        }
        apartamento.marcarOcupado();

        reservaRepository.guardar(reserva);
        apartamentoRepository.guardar(apartamento);
    }
}
