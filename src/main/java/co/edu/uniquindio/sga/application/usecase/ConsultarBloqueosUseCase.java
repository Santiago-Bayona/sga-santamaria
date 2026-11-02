package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarBloqueosUseCase {

    private final BloqueoRepository bloqueoRepository;

    public ConsultarBloqueosUseCase(BloqueoRepository bloqueoRepository) {
        this.bloqueoRepository = bloqueoRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<Bloqueo> ejecutar(IdentificacionApartamento identificacion, int pagina) {

        if (pagina < 0) {
            throw new SolicitudAplicacionException("El número de página no puede ser negativo.");
        }

        return bloqueoRepository.buscarVigentesPorApartamento(identificacion, pagina);
    }
}