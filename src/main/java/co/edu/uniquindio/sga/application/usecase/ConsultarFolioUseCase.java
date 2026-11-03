package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.FolioNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarFolioUseCase {

    private final FolioRepository folioRepository;

    public ConsultarFolioUseCase(FolioRepository folioRepository) {
        this.folioRepository = folioRepository;
    }

    @Transactional(readOnly = true)
    public Folio ejecutar(CodigoReserva codigo) {

        return folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new FolioNoEncontradoException(codigo));
    }
}