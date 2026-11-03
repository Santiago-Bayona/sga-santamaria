package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.Alojamiento;
import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.AlojamientoJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Puerto del alojamiento singleton. obtener() lanza EntidadNoEncontradaException si aun no hay semilla.
 */

@Repository
@Transactional(readOnly = true)
public class AlojamientoRepositoryAdapter implements AlojamientoRepository {

    private final AlojamientoJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public AlojamientoRepositoryAdapter(AlojamientoJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Alojamiento obtener() {
        return jpa.findAll().stream().findFirst()
                .map(mapper::aDominio)
                .orElseThrow(() -> new EntidadNoEncontradaException("No hay un alojamiento configurado en el sistema."));
    }

    @Override
    @Transactional
    public void guardar(Alojamiento alojamiento) {
        jpa.save(mapper.aEntidad(alojamiento));
    }
}
