package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.CanalExternoYaRegistradoException;
import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.repository.CanalExternoRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarCanalExternoUseCase {

    private final CanalExternoRepository canalRepository;

    public RegistrarCanalExternoUseCase(CanalExternoRepository canalRepository) {
        this.canalRepository = canalRepository;
    }

    @Transactional
    public CanalExterno ejecutar(CodigoCanalExterno codigo, String tipo, String credencial) {

        if (canalRepository.obtenerPorCodigo(codigo).isPresent()) {
            throw new CanalExternoYaRegistradoException(codigo);
        }

        // Nace activo. Guardar la credencial de forma segura (cifrada o con hash) es responsabilidad de infraestructura.
        CanalExterno canal = new CanalExterno(codigo, tipo, credencial);
        canalRepository.guardar(canal);
        return canal;
    }
}
