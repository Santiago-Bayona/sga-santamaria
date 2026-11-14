package co.edu.uniquindio.sga.infrastructure.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Consultas de los enlaces de recuperación de contraseña. */
public interface TokenRecuperacionJpaRepository extends JpaRepository<TokenRecuperacionJpaEntity, String> {

    List<TokenRecuperacionJpaEntity> findByUsernameAndUsadoFalse(String username);
}
