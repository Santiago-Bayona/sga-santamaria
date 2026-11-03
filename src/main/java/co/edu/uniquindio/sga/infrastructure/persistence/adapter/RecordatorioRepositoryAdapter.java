package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.Recordatorio;
import co.edu.uniquindio.sga.domain.repository.RecordatorioRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.EstadoRecordatorio;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.RecordatorioJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto RecordatorioRepository. Expone los programados cuya fecha ya llego para el scheduler.
 */

@Repository
public class RecordatorioRepositoryAdapter implements RecordatorioRepository {

    private final RecordatorioJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public RecordatorioRepositoryAdapter(RecordatorioJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void guardar(Recordatorio recordatorio) {
        jpa.save(mapper.aEntidad(recordatorio));
    }

    @Override
    public List<Recordatorio> buscarPorReserva(CodigoReserva reserva) {
        return jpa.findByCodigoReserva(reserva.valor()).stream().map(mapper::aDominio).toList();
    }

    @Override
    public List<Recordatorio> buscarPorEjecutar(LocalDateTime ahora) {
        return jpa.findByEstadoAndFechaRecordatorioLessThanEqual(EstadoRecordatorio.PROGRAMADO.name(), ahora)
                .stream().map(mapper::aDominio).toList();
    }
}
