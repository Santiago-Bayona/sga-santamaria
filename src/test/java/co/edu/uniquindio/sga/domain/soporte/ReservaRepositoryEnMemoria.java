package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Puerto ReservaRepository en memoria; imita el contrato: "activas" = PENDIENTE, CONFIRMADA o EN_CURSO. */
public class ReservaRepositoryEnMemoria implements ReservaRepository {

    private final Map<CodigoReserva, Reserva> reservas = new LinkedHashMap<>();
    public int guardados = 0;

    public ReservaRepositoryEnMemoria con(Reserva... nuevas) {
        for (Reserva reserva : nuevas) {
            reservas.put(reserva.getCodigo(), reserva);
        }
        return this;
    }

    @Override
    public Optional<Reserva> obtenerPorCodigo(CodigoReserva codigo) {
        return Optional.ofNullable(reservas.get(codigo));
    }

    @Override
    public void guardar(Reserva reserva) {
        guardados++;
        reservas.put(reserva.getCodigo(), reserva);
    }

    @Override
    public List<Reserva> buscarPorEstado(EstadoReserva estado) {
        return reservas.values().stream().filter(r -> r.getEstado() == estado).toList();
    }

    @Override
    public List<Reserva> buscarActivasPorApartamento(IdentificacionApartamento apartamento) {
        return reservas.values().stream()
                .filter(r -> r.getApartamento().equals(apartamento) && r.estaActiva()).toList();
    }

    @Override
    public Optional<Reserva> obtenerPorIdentificadorExterno(CanalOrigen canal, IdentificadorExterno id) {
        return reservas.values().stream()
                .filter(r -> r.getCanalOrigen() == canal && id.equals(r.getIdentificadorExterno())).findFirst();
    }

    @Override
    public List<Reserva> buscarPorTitular(CodigoTitular titular) {
        return reservas.values().stream().filter(r -> r.getTitular().equals(titular)).toList();
    }

    @Override
    public Pagina<Reserva> buscar(CriterioBusquedaReserva criterio, int pagina) {
        return new Pagina<>(List.copyOf(reservas.values()), pagina, reservas.size());
    }
}
