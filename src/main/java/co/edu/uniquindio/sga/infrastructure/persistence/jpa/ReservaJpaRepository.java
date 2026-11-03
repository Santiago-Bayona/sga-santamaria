package co.edu.uniquindio.sga.infrastructure.persistence.jpa;

import co.edu.uniquindio.sga.infrastructure.persistence.entity.ReservaJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Consultas de reservas. El listado filtra por r.estancia.fechaEntrada porque la fecha vive en el embeddable.
 */

public interface ReservaJpaRepository extends JpaRepository<ReservaJpaEntity, String> {

    List<ReservaJpaEntity> findByEstado(String estado);

    List<ReservaJpaEntity> findByIdentificacionApartamentoAndEstadoIn(String identificacion, List<String> estados);

    Optional<ReservaJpaEntity> findByCanalOrigenAndIdentificadorExterno(String canalOrigen, String identificadorExterno);

    List<ReservaJpaEntity> findByCodigoTitular(String codigoTitular);

    /**
     * Listado con filtros opcionales (parámetro nulo = sin filtrar) y paginación, más reciente primero.
     * Las fechas filtran por fecha de entrada y nunca llegan nulas: el adaptador manda límites abiertos.
     */
    @Query("""
            select r from ReservaJpaEntity r
            where (:estado is null or r.estado = :estado)
              and (:canal is null or r.canalOrigen = :canal)
              and (:apartamento is null or r.identificacionApartamento = :apartamento)
              and (:titular is null or r.codigoTitular = :titular)
              and r.estancia.fechaEntrada >= :desde
              and r.estancia.fechaEntrada <= :hasta
            order by r.fechaCreacion desc
            """)
    Page<ReservaJpaEntity> buscar(@Param("estado") String estado,
                                  @Param("canal") String canal,
                                  @Param("apartamento") String apartamento,
                                  @Param("titular") String titular,
                                  @Param("desde") LocalDate desde,
                                  @Param("hasta") LocalDate hasta,
                                  Pageable pageable);
}
