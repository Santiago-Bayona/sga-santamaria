package co.edu.uniquindio.sga.infrastructure.security;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Consultas Spring Data de usuarios. Las usa el filtro JWT y el loader de la semilla.
 */

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, String> {
}
