package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.ConflictoCanal;
import co.edu.uniquindio.sga.domain.valueobject.CodigoConflictoCanal;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;

import java.util.List;
import java.util.Optional;

public interface ConflictoCanalRepository {

    Optional<ConflictoCanal> obtenerPorCodigo(CodigoConflictoCanal codigo);

    /** Un mismo mensaje en conflicto recibido dos veces no registra dos conflictos. */
    Optional<ConflictoCanal> obtenerPorIdentificadorExterno(IdentificadorExterno identificadorExterno);

    void guardar(ConflictoCanal conflicto);

    /** Conflictos pendientes de revisión del administrador. */
    List<ConflictoCanal> buscarSinRevisar();
}
