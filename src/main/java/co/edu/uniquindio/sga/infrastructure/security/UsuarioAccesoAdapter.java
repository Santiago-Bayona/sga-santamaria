package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import co.edu.uniquindio.sga.domain.port.UsuarioAccesoPort;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Adaptador del puerto de cuentas sobre la tabla usuarios. */
@Repository
public class UsuarioAccesoAdapter implements UsuarioAccesoPort {

    private final UsuarioJpaRepository usuarios;

    public UsuarioAccesoAdapter(UsuarioJpaRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public Optional<CuentaUsuario> obtener(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return usuarios.findById(username.trim()).map(this::aCuenta);
    }

    @Override
    public List<CuentaUsuario> listar(String rol, Boolean activo) {
        return usuarios.findAll().stream()
                .map(this::aCuenta)
                .filter(cuenta -> rol == null || rol.isBlank() || rol.equals(cuenta.rol()))
                .filter(cuenta -> activo == null || cuenta.activo() == activo)
                .sorted(Comparator.comparing(CuentaUsuario::username))
                .toList();
    }

    @Override
    public void guardar(CuentaUsuario cuenta) {
        UsuarioJpaEntity entidad = usuarios.findById(cuenta.username()).orElseGet(UsuarioJpaEntity::new);
        entidad.setUsername(cuenta.username());
        entidad.setPasswordHash(cuenta.passwordHash());
        entidad.setRol(RolUsuario.valueOf(cuenta.rol()));
        entidad.setActivo(cuenta.activo());
        entidad.setCodigoTitular(cuenta.codigoTitular());
        entidad.setNombreCompleto(cuenta.nombreCompleto());
        entidad.setTelefono(cuenta.telefono());
        entidad.setFotoUrl(cuenta.fotoUrl());
        usuarios.save(entidad);
    }

    private CuentaUsuario aCuenta(UsuarioJpaEntity entidad) {
        return new CuentaUsuario(
                entidad.getUsername(),
                entidad.getPasswordHash(),
                entidad.getRol().name(),
                entidad.isActivo(),
                entidad.getCodigoTitular(),
                entidad.getNombreCompleto(),
                entidad.getTelefono(),
                entidad.getFotoUrl());
    }
}
