package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.domain.port.CifradoContrasenaPort;

/** Puerto técnico de cifrado de contraseñas. El dominio no conoce usuarios. */
public interface PasswordHasherPort extends CifradoContrasenaPort {
}
