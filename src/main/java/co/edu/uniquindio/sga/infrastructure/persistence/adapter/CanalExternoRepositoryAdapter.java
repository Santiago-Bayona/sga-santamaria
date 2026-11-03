package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.repository.CanalExternoRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.CanalExternoJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Puerto CanalExternoRepository. La credencial persistida la comprueba el adaptador web, no el dominio.
 */

@Repository
public class CanalExternoRepositoryAdapter implements CanalExternoRepository {

    private final CanalExternoJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public CanalExternoRepositoryAdapter(CanalExternoJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<CanalExterno> obtenerPorCodigo(CodigoCanalExterno codigo) {
        return jpa.findById(codigo.valor()).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public void guardar(CanalExterno canal) {
        jpa.save(mapper.aEntidad(canal));
    }

    @Override
    public List<CanalExterno> buscarTodos() {
        return jpa.findAll().stream().map(mapper::aDominio).toList();
    }
}
