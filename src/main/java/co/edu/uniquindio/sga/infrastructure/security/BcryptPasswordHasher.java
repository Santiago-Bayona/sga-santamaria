package co.edu.uniquindio.sga.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cifra y verifica claves con BCrypt. Implementa el puerto tecnico PasswordHasherPort.
 */

@Component
public class BcryptPasswordHasher implements PasswordHasherPort {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String cifrar(String textoPlano) {
        return encoder.encode(textoPlano);
    }

    @Override
    public boolean coincide(String textoPlano, String hash) {
        return encoder.matches(textoPlano, hash);
    }
}
