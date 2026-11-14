package co.edu.uniquindio.sga.domain.port;

import java.util.List;
import java.util.Optional;

/** Puerto de las cuentas de acceso. El rol y el estado nulos significan "sin filtrar". */
public interface UsuarioAccesoPort {

    Optional<CuentaUsuario> obtener(String username);

    List<CuentaUsuario> listar(String rol, Boolean activo);

    void guardar(CuentaUsuario cuenta);
}
