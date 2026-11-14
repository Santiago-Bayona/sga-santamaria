package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import co.edu.uniquindio.sga.domain.port.UsuarioAccesoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarPerfilUseCase {

    private final UsuarioAccesoPort usuarios;

    public ActualizarPerfilUseCase(UsuarioAccesoPort usuarios) {
        this.usuarios = usuarios;
    }

    @Transactional
    public CuentaUsuario ejecutar(String username, String nombre, String telefono) {
        String nombreLimpio = nombre == null ? "" : nombre.trim();
        String telefonoLimpio = telefono == null ? "" : telefono.trim();
        if (nombreLimpio.isBlank() || nombreLimpio.length() > 255) {
            throw new SolicitudAplicacionException("El nombre es obligatorio y admite hasta 255 caracteres.");
        }
        if (!telefonoLimpio.matches("^[0-9+\\s()-]{7,30}$")) {
            throw new SolicitudAplicacionException("El teléfono debe tener entre 7 y 30 dígitos o separadores.");
        }
        CuentaUsuario cuenta = usuarios.obtener(username)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el usuario " + username + "."));
        CuentaUsuario actualizada = cuenta.conPerfil(nombreLimpio, telefonoLimpio);
        usuarios.guardar(actualizada);
        return actualizada;
    }
}
