package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.Novedad;
import co.edu.uniquindio.sga.domain.repository.NovedadRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoNovedad;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.NovedadJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Puerto NovedadRepository. Busca por código y por apartamento.
 */

@Repository
public class NovedadRepositoryAdapter implements NovedadRepository {

    private final NovedadJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public NovedadRepositoryAdapter(NovedadJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Novedad> obtenerPorCodigo(CodigoNovedad codigo) {
        return jpa.findById(codigo.valor()).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public void guardar(Novedad novedad) {
        jpa.save(mapper.aEntidad(novedad));
    }

    @Override
    public List<Novedad> buscarPorApartamento(IdentificacionApartamento apartamento) {
        return jpa.findByIdentificacionApartamento(apartamento.valor()).stream().map(mapper::aDominio).toList();
    }
}
