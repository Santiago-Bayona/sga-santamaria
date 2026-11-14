package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.port.CifradoContrasenaPort;
import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import co.edu.uniquindio.sga.domain.port.UsuarioAccesoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
public class CambiarContrasenaUseCase {

    /** La misma regla del registro público: mínimo 8 caracteres, una mayúscula y un número. */
    public static final Pattern CLAVE = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,}$");

    private final UsuarioAccesoPort usuarios;
    private final CifradoContrasenaPort cifrado;

    public CambiarContrasenaUseCase(UsuarioAccesoPort usuarios, CifradoContrasenaPort cifrado) {
        this.usuarios = usuarios;
        this.cifrado = cifrado;
    }

    @Transactional
    public void ejecutar(String username, String contrasenaActual, String contrasenaNueva) {
        CuentaUsuario cuenta = usuarios.obtener(username)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el usuario " + username + "."));
        if (contrasenaActual == null || !cifrado.coincide(contrasenaActual, cuenta.passwordHash())) {
            throw new SolicitudAplicacionException("La contraseña actual no coincide.");
        }
        if (contrasenaNueva == null || !CLAVE.matcher(contrasenaNueva).matches()) {
            throw new SolicitudAplicacionException(
                    "La contraseña debe tener mínimo 8 caracteres, al menos una mayúscula y un número.");
        }
        usuarios.guardar(cuenta.conClave(cifrado.cifrar(contrasenaNueva)));
    }
}
