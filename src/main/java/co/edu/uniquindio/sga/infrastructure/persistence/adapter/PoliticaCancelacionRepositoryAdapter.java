package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.PoliticaCancelacionJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Puerto de la política de cancelación. Localiza la versión vigente y persiste una versión nueva.
 */

@Repository
@Transactional(readOnly = true)
public class PoliticaCancelacionRepositoryAdapter implements PoliticaCancelacionRepository {

    private final PoliticaCancelacionJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public PoliticaCancelacionRepositoryAdapter(PoliticaCancelacionJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public PoliticaCancelacion obtenerVigente() {
        return jpa.findByVigenteTrue()
                .map(mapper::aDominio)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe una política de cancelación vigente."));
    }

    @Override
    public Optional<PoliticaCancelacion> obtenerPorVersion(VersionPolitica version) {
        return jpa.findByVersion(version.numero()).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public void guardar(PoliticaCancelacion politica) {
        jpa.save(mapper.aEntidad(politica));
    }
}
