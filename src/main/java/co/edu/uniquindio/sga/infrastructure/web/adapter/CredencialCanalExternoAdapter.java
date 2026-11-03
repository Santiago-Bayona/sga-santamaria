package co.edu.uniquindio.sga.infrastructure.web.adapter;

import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.application.exception.EntidadNoEncontradaException;
import co.edu.uniquindio.sga.domain.repository.CanalExternoRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import org.springframework.stereotype.Component;

/**
 * La autenticación de la credencial del canal es de infraestructura (comentario del dominio, 7.8).
 */
@Component
public class CredencialCanalExternoAdapter {

    private final CanalExternoRepository canalRepository;

    public CredencialCanalExternoAdapter(CanalExternoRepository canalRepository) {
        this.canalRepository = canalRepository;
    }

    public void exigirCredencial(String codigoCanal, String credencial) {
        CanalExterno canal = canalRepository.obtenerPorCodigo(new CodigoCanalExterno(codigoCanal))
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el canal " + codigoCanal));
        if (!canal.puedeVender()) {
            throw new SolicitudAplicacionException("El canal externo está desactivado y no puede vender.");
        }
        if (credencial == null || !canal.getCredencial().equals(credencial)) {
            throw new SolicitudAplicacionException("La credencial del canal externo no es válida.");
        }
    }
}
