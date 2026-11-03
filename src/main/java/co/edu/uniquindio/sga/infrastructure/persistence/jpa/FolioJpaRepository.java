package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.FolioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Busca el folio por el código de la reserva a la que pertenece.
 */

public interface FolioJpaRepository extends JpaRepository<FolioJpaEntity, String> {

    Optional<FolioJpaEntity> findByCodigoReserva(String codigoReserva);
}
