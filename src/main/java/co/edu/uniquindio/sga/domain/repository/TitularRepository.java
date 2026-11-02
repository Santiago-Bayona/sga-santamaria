package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;

import java.util.Optional;

public interface TitularRepository {

    Optional<Titular> obtenerPorCodigo(CodigoTitular codigo);

    void guardar(Titular titular);
}
