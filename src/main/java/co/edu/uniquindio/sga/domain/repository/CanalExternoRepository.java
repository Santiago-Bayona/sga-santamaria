package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;

import java.util.List;
import java.util.Optional;

public interface CanalExternoRepository {

    Optional<CanalExterno> obtenerPorCodigo(CodigoCanalExterno codigo);

    void guardar(CanalExterno canal);

    List<CanalExterno> buscarTodos();
}
