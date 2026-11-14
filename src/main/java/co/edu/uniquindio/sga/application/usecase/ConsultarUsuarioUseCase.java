package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import co.edu.uniquindio.sga.domain.port.UsuarioAccesoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarUsuarioUseCase {

    private final UsuarioAccesoPort usuarios;

    public ConsultarUsuarioUseCase(UsuarioAccesoPort usuarios) {
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public CuentaUsuario ejecutar(String username) {
        return usuarios.obtener(username)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el usuario " + username + "."));
    }
}
