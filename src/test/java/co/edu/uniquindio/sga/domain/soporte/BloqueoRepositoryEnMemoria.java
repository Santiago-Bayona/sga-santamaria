package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.repository.BloqueoRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoBloqueo;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BloqueoRepositoryEnMemoria implements BloqueoRepository {

    private final List<Bloqueo> bloqueos = new ArrayList<>();

    @Override
    public Optional<Bloqueo> obtenerPorCodigo(CodigoBloqueo codigo) {
        return bloqueos.stream().filter(b -> b.getCodigo().equals(codigo)).findFirst();
    }

    @Override
    public void guardar(Bloqueo bloqueo) {
        bloqueos.removeIf(b -> b.getCodigo().equals(bloqueo.getCodigo()));
        bloqueos.add(bloqueo);
    }

    @Override
    public List<Bloqueo> buscarVigentesPorApartamento(IdentificacionApartamento apartamento) {
        return bloqueos.stream().filter(b -> b.getApartamento().equals(apartamento) && b.estaVigente()).toList();
    }

    @Override
    public Pagina<Bloqueo> buscarVigentesPorApartamento(IdentificacionApartamento apartamento, int pagina) {
        List<Bloqueo> vigentes = buscarVigentesPorApartamento(apartamento);
        return new Pagina<>(vigentes, pagina, vigentes.size());
    }
}
