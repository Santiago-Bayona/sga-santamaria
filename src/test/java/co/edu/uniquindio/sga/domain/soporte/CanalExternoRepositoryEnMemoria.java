package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.repository.CanalExternoRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CanalExternoRepositoryEnMemoria implements CanalExternoRepository {

    private final Map<CodigoCanalExterno, CanalExterno> canales = new LinkedHashMap<>();

    public CanalExternoRepositoryEnMemoria con(CanalExterno... nuevos) {
        for (CanalExterno canal : nuevos) {
            guardar(canal);
        }
        return this;
    }

    @Override
    public Optional<CanalExterno> obtenerPorCodigo(CodigoCanalExterno codigo) {
        return Optional.ofNullable(canales.get(codigo));
    }

    @Override
    public void guardar(CanalExterno canal) {
        canales.put(canal.getCodigo(), canal);
    }

    @Override
    public List<CanalExterno> buscarTodos() {
        return List.copyOf(canales.values());
    }
}
