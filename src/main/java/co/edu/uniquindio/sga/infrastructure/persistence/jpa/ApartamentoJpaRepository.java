package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.ApartamentoJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Apartamentos activos y por estado operativo.
 */

public interface ApartamentoJpaRepository extends JpaRepository<ApartamentoJpaEntity, String> {

    List<ApartamentoJpaEntity> findByActivoTrue();

    Page<ApartamentoJpaEntity> findByActivoTrue(Pageable pageable);

    List<ApartamentoJpaEntity> findByEstadoOperativo(String estadoOperativo);
}
