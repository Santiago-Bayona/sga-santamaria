package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.AlojamientoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Tabla del alojamiento singleton. El adaptador toma la primera fila.
 */

public interface AlojamientoJpaRepository extends JpaRepository<AlojamientoJpaEntity, String> {
}
