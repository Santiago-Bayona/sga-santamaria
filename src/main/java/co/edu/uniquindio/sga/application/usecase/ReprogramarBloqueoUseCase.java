package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.BloqueoNoEncontradoException;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.service.GestionBloqueoApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReprogramarBloqueoUseCase {

    private final GestionBloqueoApartamentoService gestionBloqueoApartamentoService;
    private final BloqueoRepository bloqueoRepository;

    public ReprogramarBloqueoUseCase(GestionBloqueoApartamentoService gestionBloqueoApartamentoService,
                                     BloqueoRepository bloqueoRepository) {
        this.gestionBloqueoApartamentoService = gestionBloqueoApartamentoService;
        this.bloqueoRepository = bloqueoRepository;
    }

    @Transactional
    public void ejecutar(CodigoBloqueo codigo, Estancia nuevoRango) {

        bloqueoRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new BloqueoNoEncontradoException(codigo));

        gestionBloqueoApartamentoService.reprogramarBloqueo(codigo, nuevoRango);
    }
}