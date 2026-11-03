package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.EstadoOperativo;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.ApartamentoJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.ApartamentoJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Puerto ApartamentoRepository. La lectura es transaccional para recorrer galería, dotacion y tarifas perezosas.
 */

@Repository
@Transactional(readOnly = true)
public class ApartamentoRepositoryAdapter implements ApartamentoRepository {

    private final ApartamentoJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public ApartamentoRepositoryAdapter(ApartamentoJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Apartamento> obtenerPorIdentificacion(IdentificacionApartamento identificacion) {
        return jpa.findById(identificacion.valor()).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public void guardar(Apartamento apartamento) {
        jpa.save(mapper.aEntidad(apartamento));
    }

    @Override
    public List<Apartamento> buscarActivos() {
        return jpa.findByActivoTrue().stream().map(mapper::aDominio).toList();
    }

    @Override
    public Pagina<Apartamento> buscarActivos(int pagina) {
        Page<ApartamentoJpaEntity> resultado = jpa.findByActivoTrue(
                PageRequest.of(pagina, Pagina.TAMANO, Sort.by("identificacion")));
        return new Pagina<>(
                resultado.getContent().stream().map(mapper::aDominio).toList(),
                pagina,
                resultado.getTotalElements());
    }

    @Override
    public List<Apartamento> buscarPorEstadoOperativo(EstadoOperativo estado) {
        return jpa.findByEstadoOperativo(estado.name()).stream().map(mapper::aDominio).toList();
    }
}
