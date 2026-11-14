package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.EventoBitacoraCanalJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoBitacoraCanalJpaRepository extends JpaRepository<EventoBitacoraCanalJpaEntity, String> {

    Page<EventoBitacoraCanalJpaEntity> findAllByOrderByOcurridoEnDesc(Pageable pageable);
}
