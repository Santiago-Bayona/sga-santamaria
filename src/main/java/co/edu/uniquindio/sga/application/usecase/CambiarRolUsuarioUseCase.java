package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import co.edu.uniquindio.sga.domain.port.UsuarioAccesoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class CambiarRolUsuarioUseCase {

    public static final Set<String> ROLES = Set.of("ADMIN", "RECEPCION", "SERVICIO", "CANAL", "HUESPED");

    private final UsuarioAccesoPort usuarios;

    public CambiarRolUsuarioUseCase(UsuarioAccesoPort usuarios) {
        this.usuarios = usuarios;
    }

    @Transactional
    public CuentaUsuario ejecutar(String username, String rol) {
        if (rol == null || !ROLES.contains(rol.trim())) {
            throw new SolicitudAplicacionException("El rol no es válido.");
        }
        CuentaUsuario cuenta = usuarios.obtener(username)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el usuario " + username + "."));
        CuentaUsuario actualizada = cuenta.conRol(rol.trim());
        usuarios.guardar(actualizada);
        return actualizada;
    }
}
