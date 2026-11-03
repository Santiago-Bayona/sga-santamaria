package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.repository.CanalExternoRepository;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarCanalesExternosUseCase {

    private final CanalExternoRepository canalRepository;

    public ConsultarCanalesExternosUseCase(CanalExternoRepository canalRepository) {
        this.canalRepository = canalRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<CanalExterno> ejecutar(int pagina) {

        return PaginacionEnMemoria.de(canalRepository.buscarTodos(), pagina);
    }
}
