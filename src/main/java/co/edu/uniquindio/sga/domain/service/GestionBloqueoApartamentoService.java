package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.MotivoBloqueo;

import java.time.LocalDateTime;

/**
 * 7.3: no puede registrarse (ni moverse) un bloqueo sobre noches con reservas activas. Una vez vigente,
 * deja el apartamento no disponible en esas noches (RN-07). Bloqueo no puede ver las reservas.
 */
public class GestionBloqueoApartamentoService {

    private final BloqueoRepository bloqueoRepository;
    private final ReservaRepository reservaRepository;
    private final ApartamentoRepository apartamentoRepository;

    public GestionBloqueoApartamentoService(BloqueoRepository bloqueoRepository, ReservaRepository reservaRepository,
                                            ApartamentoRepository apartamentoRepository) {
        this.bloqueoRepository = bloqueoRepository;
        this.reservaRepository = reservaRepository;
        this.apartamentoRepository = apartamentoRepository;
    }

    public Bloqueo registrarBloqueo(CodigoBloqueo codigo, IdentificacionApartamento apartamento, Estancia rango,
                                    MotivoBloqueo motivo, LocalDateTime ahora) {
        if (apartamentoRepository.obtenerPorIdentificacion(apartamento).isEmpty()) {
            throw new ReglaDominioException("No existe el apartamento " + apartamento.valor());
        }
        exigirSinReservasActivas(apartamento, rango);
        Bloqueo bloqueo = new Bloqueo(codigo, apartamento, rango, motivo, ahora);
        bloqueoRepository.guardar(bloqueo);
        return bloqueo;
    }

    public void reprogramarBloqueo(CodigoBloqueo codigo, Estancia nuevoRango) {
        Bloqueo bloqueo = bloqueo(codigo);
        exigirSinReservasActivas(bloqueo.getApartamento(), nuevoRango);
        bloqueo.reprogramar(nuevoRango);
        bloqueoRepository.guardar(bloqueo);
    }

    /** Levantar no requiere validar reservas: solo libera noches. Nunca se borra (12.4). */
    public void levantarBloqueo(CodigoBloqueo codigo) {
        Bloqueo bloqueo = bloqueo(codigo);
        bloqueo.levantar();
        bloqueoRepository.guardar(bloqueo);
    }

    private void exigirSinReservasActivas(IdentificacionApartamento apartamento, Estancia rango) {
        if (rango == null) {
            throw new ReglaDominioException("El rango del bloqueo es obligatorio.");
        }
        boolean hayReservas = reservaRepository.buscarActivasPorApartamento(apartamento).stream()
                .anyMatch(reserva -> reserva.seSolapaCon(rango));
        if (hayReservas) {
            throw new ReglaDominioException("No puede bloquearse un rango con reservas activas en esas noches.");
        }
    }

    private Bloqueo bloqueo(CodigoBloqueo codigo) {
        return bloqueoRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReglaDominioException("No existe el bloqueo " + codigo.valor()));
    }
}
