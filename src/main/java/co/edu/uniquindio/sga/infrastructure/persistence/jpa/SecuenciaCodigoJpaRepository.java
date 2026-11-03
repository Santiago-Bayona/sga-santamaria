package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.SecuenciaCodigoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contador anual usado por el generador de códigos RES-año-consecutivo.
 */

public interface SecuenciaCodigoJpaRepository extends JpaRepository<SecuenciaCodigoJpaEntity, String> {
}
