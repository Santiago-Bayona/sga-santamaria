package co.edu.uniquindio.sga.domain.port;

/**
 * Cuenta de acceso al sistema. No es un agregado del alojamiento: el titular del negocio sigue siendo otra cosa.
 * El hash de la contraseña viaja con la cuenta para que el caso de uso pueda cambiarla sin conocer el algoritmo.
 */
public record CuentaUsuario(
        String username,
        String passwordHash,
        String rol,
        boolean activo,
        String codigoTitular,
        String nombreCompleto,
        String telefono,
        String fotoUrl
) {

    public CuentaUsuario conPerfil(String nombreCompleto, String telefono) {
        return new CuentaUsuario(username, passwordHash, rol, activo, codigoTitular, nombreCompleto, telefono, fotoUrl);
    }

    public CuentaUsuario conFoto(String fotoUrl) {
        return new CuentaUsuario(username, passwordHash, rol, activo, codigoTitular, nombreCompleto, telefono, fotoUrl);
    }

    public CuentaUsuario conRol(String rol) {
        return new CuentaUsuario(username, passwordHash, rol, activo, codigoTitular, nombreCompleto, telefono, fotoUrl);
    }

    public CuentaUsuario inactiva() {
        return new CuentaUsuario(username, passwordHash, rol, false, codigoTitular, nombreCompleto, telefono, fotoUrl);
    }

    public CuentaUsuario conClave(String passwordHash) {
        return new CuentaUsuario(username, passwordHash, rol, activo, codigoTitular, nombreCompleto, telefono, fotoUrl);
    }
}
