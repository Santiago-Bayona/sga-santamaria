package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.NovedadJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Consultas de novedades por apartamento.
 */

public interface NovedadJpaRepository extends JpaRepository<NovedadJpaEntity, String> {

    List<NovedadJpaEntity> findByIdentificacionApartamento(String identificacion);
}
