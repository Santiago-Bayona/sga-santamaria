package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.ReservaJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.ReservaJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto ReservaRepository sobre JPA. Lista con Pageable, orden fechaCreacion DESC y 10 por página. La transacción de lectura mantiene la sesión abierta al mapear colecciones.
 */

@Repository
@Transactional(readOnly = true)
public class ReservaRepositoryAdapter implements ReservaRepository {

    private static final List<String> ESTADOS_ACTIVOS = List.of(
            EstadoReserva.PENDIENTE.name(),
            EstadoReserva.CONFIRMADA.name(),
            EstadoReserva.EN_CURSO.name());

    private static final LocalDate FECHA_MINIMA = LocalDate.of(1900, 1, 1);
    private static final LocalDate FECHA_MAXIMA = LocalDate.of(9999, 12, 31);

    private final ReservaJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public ReservaRepositoryAdapter(ReservaJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Reserva> obtenerPorCodigo(CodigoReserva codigo) {
        return jpa.findById(codigo.valor()).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public void guardar(Reserva reserva) {
        jpa.save(mapper.aEntidad(reserva));
    }

    @Override
    public List<Reserva> buscarPorEstado(EstadoReserva estado) {
        return jpa.findByEstado(estado.name()).stream().map(mapper::aDominio).toList();
    }

    @Override
    public List<Reserva> buscarActivasPorApartamento(IdentificacionApartamento apartamento) {
        return jpa.findByIdentificacionApartamentoAndEstadoIn(apartamento.valor(), ESTADOS_ACTIVOS)
                .stream().map(mapper::aDominio).toList();
    }

    @Override
    public Optional<Reserva> obtenerPorIdentificadorExterno(CanalOrigen canal, IdentificadorExterno identificadorExterno) {
        return jpa.findByCanalOrigenAndIdentificadorExterno(canal.name(), identificadorExterno.valor()).map(mapper::aDominio);
    }

    @Override
    public List<Reserva> buscarPorTitular(CodigoTitular titular) {
        return jpa.findByCodigoTitular(titular.valor()).stream().map(mapper::aDominio).toList();
    }

    @Override
    public Pagina<Reserva> buscar(CriterioBusquedaReserva criterio, int pagina) {
        Page<ReservaJpaEntity> resultado = jpa.buscar(
                criterio.estado() == null ? null : criterio.estado().name(),
                criterio.canal() == null ? null : criterio.canal().name(),
                criterio.apartamento() == null ? null : criterio.apartamento().valor(),
                criterio.titular() == null ? null : criterio.titular().valor(),
                criterio.fechaDesde() == null ? FECHA_MINIMA : criterio.fechaDesde(),
                criterio.fechaHasta() == null ? FECHA_MAXIMA : criterio.fechaHasta(),
                PageRequest.of(pagina, Pagina.TAMANO, Sort.by(Sort.Direction.DESC, "fechaCreacion")));
        return new Pagina<>(
                resultado.getContent().stream().map(mapper::aDominio).toList(),
                pagina,
                resultado.getTotalElements());
    }
}
