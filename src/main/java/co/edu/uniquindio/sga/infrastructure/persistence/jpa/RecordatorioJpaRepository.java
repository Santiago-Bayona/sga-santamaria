package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.RecordatorioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Busca recordatorios de una reserva y los programados vencidos.
 */

public interface RecordatorioJpaRepository extends JpaRepository<RecordatorioJpaEntity, String> {

    List<RecordatorioJpaEntity> findByCodigoReserva(String codigoReserva);

    List<RecordatorioJpaEntity> findByEstadoAndFechaRecordatorioLessThanEqual(String estado, LocalDateTime ahora);
}
