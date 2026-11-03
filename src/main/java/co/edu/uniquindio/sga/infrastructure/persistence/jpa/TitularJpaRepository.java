package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.TitularJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso Spring Data a titulares. El adaptador es el único cliente.
 */

public interface TitularJpaRepository extends JpaRepository<TitularJpaEntity, String> {
}
