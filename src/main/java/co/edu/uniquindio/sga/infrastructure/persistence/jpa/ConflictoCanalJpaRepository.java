package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.ConflictoCanalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Consultas de conflictos de canal, incluido el pendiente de revision.
 */

public interface ConflictoCanalJpaRepository extends JpaRepository<ConflictoCanalJpaEntity, String> {

    Optional<ConflictoCanalJpaEntity> findByIdentificadorExterno(String identificadorExterno);

    List<ConflictoCanalJpaEntity> findByRevisadoFalse();
}
