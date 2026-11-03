package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.TitularJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Puerto TitularRepository. Traduce el titular de dominio a su fila JPA y de vuelta.
 */

@Repository
public class TitularRepositoryAdapter implements TitularRepository {

    private final TitularJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public TitularRepositoryAdapter(TitularJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Titular> obtenerPorCodigo(CodigoTitular codigo) {
        return jpa.findById(codigo.valor()).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public void guardar(Titular titular) {
        jpa.save(mapper.aEntidad(titular));
    }
}
