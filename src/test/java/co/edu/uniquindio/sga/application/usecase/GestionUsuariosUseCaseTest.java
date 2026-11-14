package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.application.exception.UsuarioConReservasActivasException;
import co.edu.uniquindio.sga.domain.port.CifradoContrasenaPort;
import co.edu.uniquindio.sga.domain.port.CuentaUsuario;
import co.edu.uniquindio.sga.domain.port.UsuarioAccesoPort;
import co.edu.uniquindio.sga.domain.soporte.DatosDePrueba;
import co.edu.uniquindio.sga.domain.soporte.ReservaRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Gestión de cuentas (7.2): cada caso de uso con su prueba, sin Spring. */
class GestionUsuariosUseCaseTest {

    private UsuariosEnMemoria usuarios;
    private ReservaRepositoryEnMemoria reservas;

    @BeforeEach
    void preparar() {
        usuarios = new UsuariosEnMemoria();
        reservas = new ReservaRepositoryEnMemoria();
        usuarios.guardar(cuenta("ana@correo.com", "HUESPED", true, "TIT-1"));
        usuarios.guardar(cuenta("admin@correo.com", "ADMIN", true, null));
    }

    @Test
    @DisplayName("listar filtra por rol y por estado")
    void listar() {
        List<CuentaUsuario> admins = new ListarUsuariosUseCase(usuarios).ejecutar("ADMIN", true);
        assertEquals(1, admins.size());
        assertEquals("admin@correo.com", admins.get(0).username());
    }

    @Test
    @DisplayName("consultar devuelve la cuenta y falla si no existe")
    void consultar() {
        assertEquals("HUESPED", new ConsultarUsuarioUseCase(usuarios).ejecutar("ana@correo.com").rol());
        assertThrows(EntidadNoEncontradaException.class,
                () -> new ConsultarUsuarioUseCase(usuarios).ejecutar("nadie@correo.com"));
    }

    @Test
    @DisplayName("cambiar rol rechaza un rol desconocido y persiste uno válido")
    void cambiarRol() {
        CambiarRolUsuarioUseCase caso = new CambiarRolUsuarioUseCase(usuarios);
        assertThrows(SolicitudAplicacionException.class, () -> caso.ejecutar("ana@correo.com", "GERENTE"));
        assertEquals("RECEPCION", caso.ejecutar("ana@correo.com", "RECEPCION").rol());
    }

    @Test
    @DisplayName("desactivar rechaza al usuario que tiene reservas activas")
    void desactivarConReservasActivas() {
        reservas.con(DatosDePrueba.reservaPendiente("RES-1", DatosDePrueba.apartamento("APT-101", 4),
                DatosDePrueba.estancia(DatosDePrueba.HOY.plusDays(10), 2)));
        DesactivarUsuarioUseCase caso = new DesactivarUsuarioUseCase(usuarios, reservas);
        assertThrows(UsuarioConReservasActivasException.class, () -> caso.ejecutar("ana@correo.com"));
        assertTrue(usuarios.obtener("ana@correo.com").orElseThrow().activo());
    }

    @Test
    @DisplayName("desactivar procede cuando las reservas del titular ya no están activas")
    void desactivarSinReservasActivas() {
        var reserva = DatosDePrueba.reservaPendiente("RES-1", DatosDePrueba.apartamento("APT-101", 4),
                DatosDePrueba.estancia(DatosDePrueba.HOY.plusDays(10), 2));
        reserva.cancelar(new co.edu.uniquindio.sga.domain.valueobject.MotivoCancelacion("Cambio de planes"));
        reservas.con(reserva);
        CuentaUsuario inactiva = new DesactivarUsuarioUseCase(usuarios, reservas).ejecutar("ana@correo.com");
        assertFalse(inactiva.activo());
    }

    @Test
    @DisplayName("actualizar perfil exige nombre y teléfono")
    void actualizarPerfil() {
        ActualizarPerfilUseCase caso = new ActualizarPerfilUseCase(usuarios);
        assertThrows(SolicitudAplicacionException.class, () -> caso.ejecutar("ana@correo.com", " ", "3001112233"));
        CuentaUsuario actualizada = caso.ejecutar("ana@correo.com", "Ana Gómez", "3001112233");
        assertEquals("Ana Gómez", actualizada.nombreCompleto());
        assertEquals("3001112233", actualizada.telefono());
    }

    @Test
    @DisplayName("cambiar contraseña valida la actual y aplica la regla del registro")
    void cambiarContrasena() {
        CifradoPlano cifrado = new CifradoPlano();
        usuarios.guardar(usuarios.obtener("ana@correo.com").orElseThrow().conClave(cifrado.cifrar("Clave123")));
        CambiarContrasenaUseCase caso = new CambiarContrasenaUseCase(usuarios, cifrado);
        assertThrows(SolicitudAplicacionException.class, () -> caso.ejecutar("ana@correo.com", "otra", "Nueva123"));
        assertThrows(SolicitudAplicacionException.class, () -> caso.ejecutar("ana@correo.com", "Clave123", "debil"));
        caso.ejecutar("ana@correo.com", "Clave123", "Nueva123");
        assertTrue(cifrado.coincide("Nueva123", usuarios.obtener("ana@correo.com").orElseThrow().passwordHash()));
    }

    @Test
    @DisplayName("la foto de perfil se guarda por el puerto de imágenes")
    void subirFoto() {
        byte[] jpeg = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0, 1, 2, 3};
        AlmacenFalso fotos = new AlmacenFalso();
        CuentaUsuario cuenta = new SubirFotoPerfilUseCase(usuarios, fotos).ejecutar("ana@correo.com", "image/jpeg", jpeg);
        assertTrue(cuenta.fotoUrl().startsWith("https://fotos/perfil-ana"));
    }

    private static CuentaUsuario cuenta(String username, String rol, boolean activo, String titular) {
        return new CuentaUsuario(username, "hash", rol, activo, titular, "Nombre", null, null);
    }

    private static final class UsuariosEnMemoria implements UsuarioAccesoPort {
        private final Map<String, CuentaUsuario> datos = new LinkedHashMap<>();

        @Override
        public Optional<CuentaUsuario> obtener(String username) {
            return Optional.ofNullable(datos.get(username));
        }

        @Override
        public List<CuentaUsuario> listar(String rol, Boolean activo) {
            return datos.values().stream()
                    .filter(cuenta -> rol == null || rol.isBlank() || rol.equals(cuenta.rol()))
                    .filter(cuenta -> activo == null || cuenta.activo() == activo)
                    .toList();
        }

        @Override
        public void guardar(CuentaUsuario cuenta) {
            datos.put(cuenta.username(), cuenta);
        }
    }

    private static final class CifradoPlano implements CifradoContrasenaPort {
        @Override
        public String cifrar(String textoPlano) {
            return "hash:" + textoPlano;
        }

        @Override
        public boolean coincide(String textoPlano, String hash) {
            return cifrar(textoPlano).equals(hash);
        }
    }

    private static final class AlmacenFalso implements co.edu.uniquindio.sga.domain.port.AlmacenamientoImagenesPort {
        @Override
        public String guardar(String nombre, String tipoContenido, byte[] contenido) {
            return "https://fotos/" + nombre;
        }
    }
}
