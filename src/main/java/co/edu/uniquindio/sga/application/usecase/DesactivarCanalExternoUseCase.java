package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.CanalExternoNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.repository.CanalExternoRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Un canal desactivado no puede vender sobre el inventario (sus reservas existentes no se tocan). */

@Service
public class DesactivarCanalExternoUseCase {

    private final CanalExternoRepository canalRepository;

    public DesactivarCanalExternoUseCase(CanalExternoRepository canalRepository) {
        this.canalRepository = canalRepository;
    }

    @Transactional
    public CanalExterno ejecutar(CodigoCanalExterno codigo) {

        CanalExterno canal = canalRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new CanalExternoNoEncontradoException(codigo));

        canal.desactivar();
        canalRepository.guardar(canal);
        return canal;
    }
}
