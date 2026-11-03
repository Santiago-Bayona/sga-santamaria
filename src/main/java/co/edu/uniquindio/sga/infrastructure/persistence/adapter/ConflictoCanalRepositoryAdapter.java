package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.ConflictoCanal;
import co.edu.uniquindio.sga.domain.repository.ConflictoCanalRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoConflictoCanal;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.ConflictoCanalJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de conflictos de canal. Lista los que siguen sin revisar, paginados.
 */

@Repository
public class ConflictoCanalRepositoryAdapter implements ConflictoCanalRepository {

    private final ConflictoCanalJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public ConflictoCanalRepositoryAdapter(ConflictoCanalJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<ConflictoCanal> obtenerPorCodigo(CodigoConflictoCanal codigo) {
        return jpa.findById(codigo.valor()).map(mapper::aDominio);
    }

    @Override
    public Optional<ConflictoCanal> obtenerPorIdentificadorExterno(IdentificadorExterno identificadorExterno) {
        return jpa.findByIdentificadorExterno(identificadorExterno.valor()).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public void guardar(ConflictoCanal conflicto) {
        jpa.save(mapper.aEntidad(conflicto));
    }

    @Override
    public List<ConflictoCanal> buscarSinRevisar() {
        return jpa.findByRevisadoFalse().stream().map(mapper::aDominio).toList();
    }
}
