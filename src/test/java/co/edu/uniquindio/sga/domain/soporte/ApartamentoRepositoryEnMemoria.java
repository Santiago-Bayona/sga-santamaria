package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.EstadoOperativo;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApartamentoRepositoryEnMemoria implements ApartamentoRepository {

    private final List<Apartamento> apartamentos = new ArrayList<>();

    public ApartamentoRepositoryEnMemoria con(Apartamento... nuevos) {
        apartamentos.addAll(List.of(nuevos));
        return this;
    }

    @Override
    public Optional<Apartamento> obtenerPorIdentificacion(IdentificacionApartamento identificacion) {
        return apartamentos.stream().filter(a -> a.getIdentificacion().equals(identificacion)).findFirst();
    }

    @Override
    public void guardar(Apartamento apartamento) {
        apartamentos.removeIf(a -> a.getIdentificacion().equals(apartamento.getIdentificacion()));
        apartamentos.add(apartamento);
    }

    @Override
    public List<Apartamento> buscarActivos() {
        return apartamentos.stream().filter(Apartamento::estaActivo).toList();
    }

    @Override
    public Pagina<Apartamento> buscarActivos(int pagina) {
        List<Apartamento> activos = buscarActivos();
        return new Pagina<>(activos, pagina, activos.size());
    }

    @Override
    public List<Apartamento> buscarPorEstadoOperativo(EstadoOperativo estado) {
        return apartamentos.stream().filter(a -> a.getEstadoOperativo() == estado).toList();
    }
}
