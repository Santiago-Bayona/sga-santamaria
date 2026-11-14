package co.edu.uniquindio.sga.domain.port;

/** Puerto de cifrado de contraseñas. El dominio no elige el algoritmo. */
public interface CifradoContrasenaPort {

    String cifrar(String textoPlano);

    boolean coincide(String textoPlano, String hash);
}
