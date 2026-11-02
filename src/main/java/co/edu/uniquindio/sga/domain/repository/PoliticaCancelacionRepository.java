package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.PoliticaCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.VersionPolitica;

import java.util.Optional;

public interface PoliticaCancelacionRepository {

    /** F-07: existe una sola versión vigente. */
    PoliticaCancelacion obtenerVigente();

    /** RN-13: la retención se toma de la versión congelada en la reserva. */
    Optional<PoliticaCancelacion> obtenerPorVersion(VersionPolitica version);

    void guardar(PoliticaCancelacion politica);
}
