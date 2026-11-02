package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.CanalExternoNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.repository.CanalExternoRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RotarCredencialCanalExternoUseCase {

    private final CanalExternoRepository canalRepository;

    public RotarCredencialCanalExternoUseCase(CanalExternoRepository canalRepository) {
        this.canalRepository = canalRepository;
    }

    @Transactional
    public CanalExterno ejecutar(CodigoCanalExterno codigo, String nuevaCredencial) {

        CanalExterno canal = canalRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new CanalExternoNoEncontradoException(codigo));

        canal.rotarCredencial(nuevaCredencial);
        canalRepository.guardar(canal);
        return canal;
    }
}
