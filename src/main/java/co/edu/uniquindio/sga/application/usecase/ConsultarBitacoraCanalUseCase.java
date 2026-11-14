package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.EventoBitacoraCanal;
import co.edu.uniquindio.sga.domain.repository.EventoBitacoraCanalRepository;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarBitacoraCanalUseCase {

    private final EventoBitacoraCanalRepository bitacora;

    public ConsultarBitacoraCanalUseCase(EventoBitacoraCanalRepository bitacora) {
        this.bitacora = bitacora;
    }

    @Transactional(readOnly = true)
    public Pagina<EventoBitacoraCanal> ejecutar(int pagina) {
        return bitacora.listar(pagina);
    }
}
