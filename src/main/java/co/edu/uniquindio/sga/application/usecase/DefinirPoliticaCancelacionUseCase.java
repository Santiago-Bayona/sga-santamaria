package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.valueobject.Porcentaje;
import co.edu.uniquindio.sga.domain.valueobject.TramoCancelacion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** RN-13: actualizar la política NO edita la anterior; publica una versión nueva y la anterior queda no vigente. */

@Service
public class DefinirPoliticaCancelacionUseCase {

    private final PoliticaCancelacionRepository politicaRepository;

    public DefinirPoliticaCancelacionUseCase(PoliticaCancelacionRepository politicaRepository) {
        this.politicaRepository = politicaRepository;
    }

    @Transactional
    public PoliticaCancelacion ejecutar(List<TramoCancelacion> tramos, Porcentaje retencionNoShow, LocalDateTime momentoActual) {

        PoliticaCancelacion vigente = politicaRepository.obtenerVigente();

        // El dominio valida los tramos (mínimo 2, sin repetir, uno desde 0 días) y calcula la versión siguiente.
        PoliticaCancelacion nueva = vigente.publicarNuevaVersion(tramos, retencionNoShow, momentoActual);

        politicaRepository.guardar(vigente);   // la anterior queda NO vigente
        politicaRepository.guardar(nueva);     // la nueva queda vigente
        return nueva;
    }
}
