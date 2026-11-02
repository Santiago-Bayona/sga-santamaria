package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.BloqueoNoEncontradoException;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.service.GestionBloqueoApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LevantarBloqueoUseCase {

    private final GestionBloqueoApartamentoService gestionBloqueoApartamentoService;
    private final BloqueoRepository bloqueoRepository;

    public LevantarBloqueoUseCase(GestionBloqueoApartamentoService gestionBloqueoApartamentoService,
                                  BloqueoRepository bloqueoRepository) {
        this.gestionBloqueoApartamentoService = gestionBloqueoApartamentoService;
        this.bloqueoRepository = bloqueoRepository;
    }

    @Transactional
    public void ejecutar(CodigoBloqueo codigo) {

        bloqueoRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new BloqueoNoEncontradoException(codigo));

        gestionBloqueoApartamentoService.levantarBloqueo(codigo);
    }
}