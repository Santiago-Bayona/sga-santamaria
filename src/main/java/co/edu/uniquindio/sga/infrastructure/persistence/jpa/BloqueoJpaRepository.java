package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.BloqueoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Bloqueos vigentes de un apartamento, en lista o en página.
 */

public interface BloqueoJpaRepository extends JpaRepository<BloqueoJpaEntity, String> {

    List<BloqueoJpaEntity> findByIdentificacionApartamentoAndVigenteTrue(String identificacion);

    Page<BloqueoJpaEntity> findByIdentificacionApartamentoAndVigenteTrue(String identificacion, Pageable pageable);
}
