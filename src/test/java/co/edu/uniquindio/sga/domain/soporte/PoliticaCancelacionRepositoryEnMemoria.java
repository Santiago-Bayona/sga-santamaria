package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.repository.PoliticaCancelacionRepository;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Puerto PoliticaCancelacionRepository en memoria: conserva todas las versiones (RN-13/RN-22). */
public class PoliticaCancelacionRepositoryEnMemoria implements PoliticaCancelacionRepository {

    private final List<PoliticaCancelacion> versiones = new ArrayList<>();

    public PoliticaCancelacionRepositoryEnMemoria con(PoliticaCancelacion... politicas) {
        for (PoliticaCancelacion politica : politicas) {
            guardar(politica);
        }
        return this;
    }

    @Override
    public PoliticaCancelacion obtenerVigente() {
        return versiones.stream().filter(PoliticaCancelacion::estaVigente)
                .reduce((a, b) -> b).orElseThrow();
    }

    @Override
    public Optional<PoliticaCancelacion> obtenerPorVersion(VersionPolitica version) {
        return versiones.stream().filter(p -> p.getVersion().equals(version)).findFirst();
    }

    @Override
    public void guardar(PoliticaCancelacion politica) {
        versiones.removeIf(p -> p.getVersion().equals(politica.getVersion()));
        versiones.add(politica);
    }
}
