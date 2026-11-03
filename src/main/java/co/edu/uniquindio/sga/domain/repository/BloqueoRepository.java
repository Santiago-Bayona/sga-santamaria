package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;

import java.util.List;
import java.util.Optional;

public interface BloqueoRepository {

    Optional<Bloqueo> obtenerPorCodigo(CodigoBloqueo codigo);

    void guardar(Bloqueo bloqueo);

    List<Bloqueo> buscarVigentesPorApartamento(IdentificacionApartamento apartamento);

    /** Listado paginado de los bloqueos vigentes de un apartamento. */
    Pagina<Bloqueo> buscarVigentesPorApartamento(IdentificacionApartamento apartamento, int pagina);
}
