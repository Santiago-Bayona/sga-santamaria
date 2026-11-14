package co.edu.uniquindio.sga.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea los cinco usuarios de desarrollo solo cuando la tabla está vacía. Corre después de la semilla del alojamiento.
 */

@Component
@Order(2)
@ConditionalOnProperty(name = "sga.usuarios-iniciales.habilitados", havingValue = "true", matchIfMissing = true)
public class UsuariosInicialesLoader implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(UsuariosInicialesLoader.class);
    private static final String MODO_CUENTA = "cuenta";

    private final UsuarioJpaRepository usuarios;
    private final PasswordHasherPort hasher;
    private final String modo;
    private final String correoAdmin;
    private final String contrasenaAdmin;
    private final String nombreAdmin;
    private final String telefonoAdmin;

    public UsuariosInicialesLoader(UsuarioJpaRepository usuarios, PasswordHasherPort hasher,
                                   @Value("${sga.usuarios-iniciales.modo:semilla}") String modo,
                                   @Value("${sga.usuarios-iniciales.admin.correo:}") String correoAdmin,
                                   @Value("${sga.usuarios-iniciales.admin.contrasena:}") String contrasenaAdmin,
                                   @Value("${sga.usuarios-iniciales.admin.nombre:Administrador Santamaría}") String nombreAdmin,
                                   @Value("${sga.usuarios-iniciales.admin.telefono:}") String telefonoAdmin) {
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.modo = modo;
        this.correoAdmin = correoAdmin;
        this.contrasenaAdmin = contrasenaAdmin;
        this.nombreAdmin = nombreAdmin;
        this.telefonoAdmin = telefonoAdmin;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (MODO_CUENTA.equalsIgnoreCase(modo)) {
            crearCuentaInicial();
            return;
        }
        if (usuarios.count() > 0) {
            return;
        }
        guardar("admin", "admin123", RolUsuario.ADMIN, "Administrador Santamaría", null);
        guardar("recepcion", "recepcion123", RolUsuario.RECEPCION, "Recepción", null);
        guardar("servicio", "servicio123", RolUsuario.SERVICIO, "Personal de servicio", null);
        guardar("canal", "canal123", RolUsuario.CANAL, "Canal externo", null);
        guardar("huesped", "huesped123", RolUsuario.HUESPED, "Huésped demo", "TIT-001");
        // Segundo huésped (7.2): para demostrar que un huésped no ve las reservas de otro
        guardar("huesped2", "huesped123", RolUsuario.HUESPED, "Carlos Restrepo", "TIT-002");
        LOG.info("Usuarios semilla: admin, recepcion, servicio, canal, huesped, huesped2 (claves de desarrollo).");
    }

    /**
     * Una sola cuenta ADMIN para el plan gratuito de Render, donde la base vuelve a vacío al dormir.
     * Pide correo, contraseña y teléfono. El documento y la fecha de nacimiento no aplican: el administrador no es un titular.
     */
    private void crearCuentaInicial() {
        String correo = correoAdmin == null ? "" : correoAdmin.trim().toLowerCase();
        String contrasena = contrasenaAdmin == null ? "" : contrasenaAdmin.trim();
        String telefono = telefonoAdmin == null ? "" : telefonoAdmin.trim();
        if (correo.isBlank() || contrasena.isBlank() || telefono.isBlank()) {
            throw new IllegalStateException("Complete los datos de la cuenta inicial del SGA.");
        }
        if (usuarios.existsById(correo)) {
            return;
        }
        UsuarioJpaEntity usuario = new UsuarioJpaEntity();
        usuario.setUsername(correo);
        usuario.setPasswordHash(hasher.cifrar(contrasena));
        usuario.setRol(RolUsuario.ADMIN);
        usuario.setActivo(true);
        usuario.setNombreCompleto(nombreAdmin == null || nombreAdmin.isBlank() ? "Administrador Santamaría" : nombreAdmin.trim());
        usuario.setTelefono(telefono);
        usuarios.save(usuario);
        LOG.info("Cuenta inicial del SGA creada para {}", correo);
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
