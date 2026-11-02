package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarPoliticaVigenteUseCase {

    private final PoliticaCancelacionRepository politicaRepository;

    public ConsultarPoliticaVigenteUseCase(PoliticaCancelacionRepository politicaRepository) {
        this.politicaRepository = politicaRepository;
    }

    @Transactional(readOnly = true)
    public PoliticaCancelacion ejecutar() {
        return politicaRepository.obtenerVigente();
    }
}
