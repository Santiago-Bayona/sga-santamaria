package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.BloqueoJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.BloqueoJpaRepository;
import co.edu.uniquindio.sga.infrastructure.persistence.mapper.PersistenciaMappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Puerto BloqueoRepository. La página de un apartamento se ordena por el inicio de la estancia embebida.
 */

@Repository
public class BloqueoRepositoryAdapter implements BloqueoRepository {

    private final BloqueoJpaRepository jpa;
    private final PersistenciaMappers mapper;

    public BloqueoRepositoryAdapter(BloqueoJpaRepository jpa, PersistenciaMappers mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Bloqueo> obtenerPorCodigo(CodigoBloqueo codigo) {
        return jpa.findById(codigo.valor()).map(mapper::aDominio);
    }

    @Override
    @Transactional
    public void guardar(Bloqueo bloqueo) {
        jpa.save(mapper.aEntidad(bloqueo));
    }

    @Override
    public List<Bloqueo> buscarVigentesPorApartamento(IdentificacionApartamento apartamento) {
        return jpa.findByIdentificacionApartamentoAndVigenteTrue(apartamento.valor())
                .stream().map(mapper::aDominio).toList();
    }

    @Override
    public Pagina<Bloqueo> buscarVigentesPorApartamento(IdentificacionApartamento apartamento, int pagina) {
        Page<BloqueoJpaEntity> resultado = jpa.findByIdentificacionApartamentoAndVigenteTrue(
                apartamento.valor(), PageRequest.of(pagina, Pagina.TAMANO, Sort.by("rango.fechaEntrada")));
        return new Pagina<>(
                resultado.getContent().stream().map(mapper::aDominio).toList(),
                pagina,
                resultado.getTotalElements());
    }
}
