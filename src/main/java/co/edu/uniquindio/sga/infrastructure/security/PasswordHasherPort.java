package co.edu.uniquindio.sga.infrastructure.security;

/** Puerto técnico de cifrado de contraseñas. El dominio no conoce usuarios. */
public interface PasswordHasherPort {

    String cifrar(String textoPlano);

    boolean coincide(String textoPlano, String hash);
}
