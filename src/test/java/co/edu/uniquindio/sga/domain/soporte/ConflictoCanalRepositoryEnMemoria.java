package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.ConflictoCanal;
import co.edu.uniquindio.sga.domain.repository.ConflictoCanalRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoConflictoCanal;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConflictoCanalRepositoryEnMemoria implements ConflictoCanalRepository {

    private final Map<CodigoConflictoCanal, ConflictoCanal> conflictos = new LinkedHashMap<>();

    @Override
    public Optional<ConflictoCanal> obtenerPorCodigo(CodigoConflictoCanal codigo) {
        return Optional.ofNullable(conflictos.get(codigo));
    }

    @Override
    public Optional<ConflictoCanal> obtenerPorIdentificadorExterno(IdentificadorExterno identificador) {
        return conflictos.values().stream()
                .filter(c -> c.getIdentificadorExterno().equals(identificador)).findFirst();
    }

    @Override
    public void guardar(ConflictoCanal conflicto) {
        conflictos.put(conflicto.getCodigo(), conflicto);
    }

    @Override
    public List<ConflictoCanal> buscarSinRevisar() {
        return conflictos.values().stream().filter(c -> !c.estaRevisado()).toList();
    }
}
