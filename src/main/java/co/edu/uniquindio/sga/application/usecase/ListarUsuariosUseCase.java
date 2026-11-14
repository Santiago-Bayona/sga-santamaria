package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import co.edu.uniquindio.sga.domain.port.UsuarioAccesoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarUsuariosUseCase {

    private final UsuarioAccesoPort usuarios;

    public ListarUsuariosUseCase(UsuarioAccesoPort usuarios) {
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<CuentaUsuario> ejecutar(String rol, Boolean activo) {
        return usuarios.listar(rol, activo);
    }
}
