package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.application.exception.UsuarioConReservasActivasException;
import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import co.edu.uniquindio.sga.domain.port.UsuarioAccesoPort;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DesactivarUsuarioUseCase {

    private final UsuarioAccesoPort usuarios;
    private final ReservaRepository reservas;

    public DesactivarUsuarioUseCase(UsuarioAccesoPort usuarios, ReservaRepository reservas) {
        this.usuarios = usuarios;
        this.reservas = reservas;
    }

    @Transactional
    public CuentaUsuario ejecutar(String username) {
        CuentaUsuario cuenta = usuarios.obtener(username)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el usuario " + username + "."));
        if (!cuenta.activo()) {
            throw new SolicitudAplicacionException("El usuario ya está inactivo.");
        }
        if (tieneReservasActivas(cuenta)) {
            throw new UsuarioConReservasActivasException(cuenta.username());
        }
        CuentaUsuario inactiva = cuenta.inactiva();
        usuarios.guardar(inactiva);
        return inactiva;
    }

    private boolean tieneReservasActivas(CuentaUsuario cuenta) {
        if (cuenta.codigoTitular() == null || cuenta.codigoTitular().isBlank()) {
            return false;
        }
        return reservas.buscarPorTitular(new CodigoTitular(cuenta.codigoTitular())).stream()
                .anyMatch(reserva -> reserva.getEstado().esActiva());
    }
}
