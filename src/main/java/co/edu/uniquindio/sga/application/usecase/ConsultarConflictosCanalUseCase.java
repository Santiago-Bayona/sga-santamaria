package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.ConflictoCanal;
import co.edu.uniquindio.sga.domain.repository.ConflictoCanalRepository;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarConflictosCanalUseCase {

    private final ConflictoCanalRepository conflictoCanalRepository;

    public ConsultarConflictosCanalUseCase(ConflictoCanalRepository conflictoCanalRepository) {
        this.conflictoCanalRepository = conflictoCanalRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<ConflictoCanal> ejecutar(int pagina) {

        return PaginacionEnMemoria.de(conflictoCanalRepository.buscarSinRevisar(), pagina);
    }
}