package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Novedad;
import co.edu.uniquindio.sga.domain.valueobject.CodigoNovedad;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;

import java.util.List;
import java.util.Optional;

public interface NovedadRepository {

    Optional<Novedad> obtenerPorCodigo(CodigoNovedad codigo);

    void guardar(Novedad novedad);

    /** 7.6: historial de novedades de un apartamento. */
    List<Novedad> buscarPorApartamento(IdentificacionApartamento apartamento);
}
