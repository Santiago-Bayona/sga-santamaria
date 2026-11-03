package co.edu.uniquindio.sga.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea los cinco usuarios de desarrollo solo cuando la tabla está vacía. Corre después de la semilla del alojamiento.
 */

@Component
@Order(2)
public class UsuariosInicialesLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(UsuariosInicialesLoader.class);

    private final UsuarioJpaRepository usuarios;
    private final PasswordHasherPort hasher;

    public UsuariosInicialesLoader(UsuarioJpaRepository usuarios, PasswordHasherPort hasher) {
        this.usuarios = usuarios;
        this.hasher = hasher;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarios.count() > 0) {
            return;
        }
        guardar("admin", "admin123", RolUsuario.ADMIN, "Administrador Santamaría", null);
        guardar("recepcion", "recepcion123", RolUsuario.RECEPCION, "Recepción", null);
        guardar("servicio", "servicio123", RolUsuario.SERVICIO, "Personal de servicio", null);
        guardar("canal", "canal123", RolUsuario.CANAL, "Canal externo", null);
        guardar("huesped", "huesped123", RolUsuario.HUESPED, "Huésped demo", "TIT-001");
        LOG.info("Usuarios semilla: admin, recepcion, servicio, canal, huesped (claves de desarrollo).");
    }

    private void guardar(String username, String password, RolUsuario rol, String nombre, String titular) {
        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.setUsername(username);
        usuario.setPasswordHash(hasher.cifrar(password));
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setNombreCompleto(nombre);
        usuario.setCodigoTitular(titular);
        usuarios.save(usuario);
    }
}
