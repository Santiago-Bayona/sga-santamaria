package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FolioRepositoryEnMemoria implements FolioRepository {

    private final Map<CodigoReserva, Folio> folios = new HashMap<>();

    @Override
    public Optional<Folio> obtenerPorReserva(CodigoReserva reserva) {
        return Optional.ofNullable(folios.get(reserva));
    }

    @Override
    public void guardar(Folio folio) {
        folios.put(folio.getReserva(), folio);
    }
}
