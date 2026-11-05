package co.edu.uniquindio.sga.infrastructure.security;

import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Verificación por propietario (7.2): el rol no basta, un huésped solo accede a lo que es suyo.
 * Compara el titular del recurso con el titular asociado al usuario autenticado. No decide ninguna
 * regla de negocio: solo responde quién puede ver qué. El dominio no se entera de que existen usuarios (3.6).
 * Se usa desde @PreAuthorize con el nombre "propietario".
 */
@Component("propietario")
public class PropietarioVerificador {

    private final UsuarioJpaRepository usuarios;
    private final ReservaRepository reservas;

    public PropietarioVerificador(UsuarioJpaRepository usuarios, ReservaRepository reservas) {
        this.usuarios = usuarios;
        this.reservas = reservas;
    }

    /** ¿El usuario autenticado es el titular con este código? */
    public boolean esTitular(String codigoTitular, Authentication auth) {
        if (auth == null || codigoTitular == null) {
            return false;
        }
        return usuarios.findById(auth.getName())
                .map(u -> codigoTitular.trim().equals(u.getCodigoTitular()))
                .orElse(false);
    }

    /**
     * ¿El usuario autenticado es el titular de esta reserva? Si la reserva no existe también niega:
     * así un huésped no puede averiguar qué códigos existen comparando 403 con 404.
     */
    public boolean esTitularDeReserva(String codigoReserva, Authentication auth) {
        if (codigoReserva == null || codigoReserva.isBlank()) {
            return false;
        }
        return reservas.obtenerPorCodigo(new CodigoReserva(codigoReserva))
                .map(r -> esTitular(r.getTitular().valor(), auth))
                .orElse(false);
    }

    /** 7.1: el huésped reserva solo por el canal PORTAL y solo a su propio nombre. */
    public boolean puedeReservarComoHuesped(String codigoTitular, CanalOrigen canal, Authentication auth) {
        return canal == CanalOrigen.PORTAL && esTitular(codigoTitular, auth);
    }
}
