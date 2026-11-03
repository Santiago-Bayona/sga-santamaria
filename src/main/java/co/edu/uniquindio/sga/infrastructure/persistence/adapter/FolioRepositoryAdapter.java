package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.FolioJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Puerto FolioRepository. Cargos y pagos se leen dentro de la transacción de solo lectura.
 */

@Repository
@Transactional(readOnly = true)
public class FolioRepositoryAdapter implements FolioRepository {

    private final FolioJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public FolioRepositoryAdapter(FolioJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Folio> obtenerPorReserva(CodigoReserva reserva) {
        return jpa.findByCodigoReserva(reserva.valor()).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public void guardar(Folio folio) {
        jpa.save(mapper.aEntidad(folio));
    }
}
