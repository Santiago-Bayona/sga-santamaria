package co.edu.uniquindio.sga.application.dto.response;

/** Cuenta de acceso visible por la API. Nunca incluye el hash de la contraseña. */
public record UsuarioResponse(
        String username,
        String rol,
        boolean activo,
        String codigoTitular,
        String nombreCompleto,
        String telefono,
        String fotoUrl
) {
}
