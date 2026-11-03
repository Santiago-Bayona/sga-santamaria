package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.CanalExternoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso Spring Data a canales externos.
 */

public interface CanalExternoJpaRepository extends JpaRepository<CanalExternoJpaEntity, String> {
}
