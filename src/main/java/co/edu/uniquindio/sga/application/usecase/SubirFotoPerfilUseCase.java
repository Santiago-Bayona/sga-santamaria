package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.port.AlmacenamientoImagenesPort;
import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import co.edu.uniquindio.sga.domain.port.UsuarioAccesoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class SubirFotoPerfilUseCase {

    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    private final UsuarioAccesoPort usuarios;
    private final AlmacenamientoImagenesPort almacenamiento;

    public SubirFotoPerfilUseCase(UsuarioAccesoPort usuarios, AlmacenamientoImagenesPort almacenamiento) {
        this.usuarios = usuarios;
        this.almacenamiento = almacenamiento;
    }

    @Transactional
    public CuentaUsuario ejecutar(String username, String tipoContenido, byte[] contenido) {
        String tipo = tipoContenido == null ? "" : tipoContenido.toLowerCase(Locale.ROOT);
        String extension = EXTENSIONES.get(tipo);
        if (contenido == null || contenido.length == 0) {
            throw new SolicitudAplicacionException("Selecciona una imagen.");
        }
        if (extension == null || !SubirImagenApartamentoUseCase.coincideConElTipo(tipo, contenido)) {
            throw new SolicitudAplicacionException("La imagen debe ser JPG, PNG o WebP.");
        }
        if (contenido.length > SubirImagenApartamentoUseCase.TAMANO_MAXIMO) {
            throw new SolicitudAplicacionException("La imagen supera los 5 MB.");
        }
        CuentaUsuario cuenta = usuarios.obtener(username)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el usuario " + username + "."));
        String seguro = username.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        if (seguro.isBlank()) {
            seguro = "usuario";
        }
        String url = almacenamiento.guardar("perfil-" + seguro + "-" + UUID.randomUUID() + "." + extension, tipo, contenido);
        CuentaUsuario actualizada = cuenta.conFoto(url);
        usuarios.guardar(actualizada);
        return actualizada;
    }
}
