package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

/**
 * 7.3: un apartamento se retira de la venta (baja lógica) solo si no tiene reservas activas ni futuras.
 * Toda reserva futura no terminal es activa (PENDIENTE o CONFIRMADA), así que basta con las activas.
 * El Apartamento no puede ver sus reservas: eso lo consulta el servicio.
 */
public class RetiroApartamentoService {

    private final ApartamentoRepository apartamentoRepository;
    private final ReservaRepository reservaRepository;

    public RetiroApartamentoService(ApartamentoRepository apartamentoRepository, ReservaRepository reservaRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.reservaRepository = reservaRepository;
    }

    public void retirarDeVenta(IdentificacionApartamento identificacion) {
        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(identificacion)
                .orElseThrow(() -> new ReglaDominioException("No existe el apartamento " + identificacion.valor()));
        boolean tieneReservas = !reservaRepository.buscarActivasPorApartamento(identificacion).isEmpty();
        apartamento.retirarDeVenta(tieneReservas);
        apartamentoRepository.guardar(apartamento);
    }
}
