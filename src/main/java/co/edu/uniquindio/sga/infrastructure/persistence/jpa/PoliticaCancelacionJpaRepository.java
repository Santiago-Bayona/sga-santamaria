package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.PoliticaCancelacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Localiza la política vigente o una versión concreta. La clave es código más versión.
 */

public interface PoliticaCancelacionJpaRepository
        extends JpaRepository<PoliticaCancelacionJpaEntity, PoliticaCancelacionJpaEntity.PoliticaId> {

    Optional<PoliticaCancelacionJpaEntity> findByVigenteTrue();

    Optional<PoliticaCancelacionJpaEntity> findByVersion(int version);
}
