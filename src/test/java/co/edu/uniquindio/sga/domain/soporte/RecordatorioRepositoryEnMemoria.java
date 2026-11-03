package co.edu.uniquindio.sga.domain.soporte;

import co.edu.uniquindio.sga.domain.entity.Recordatorio;
import co.edu.uniquindio.sga.domain.repository.RecordatorioRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecordatorioRepositoryEnMemoria implements RecordatorioRepository {

    private final List<Recordatorio> recordatorios = new ArrayList<>();

    @Override
    public void guardar(Recordatorio recordatorio) {
        recordatorios.removeIf(r -> r.getCodigo().equals(recordatorio.getCodigo()));
        recordatorios.add(recordatorio);
    }

    @Override
    public List<Recordatorio> buscarPorReserva(CodigoReserva reserva) {
        return recordatorios.stream().filter(r -> r.getReserva().equals(reserva)).toList();
    }

    @Override
    public List<Recordatorio> buscarPorEjecutar(LocalDateTime ahora) {
        return recordatorios.stream().filter(r -> r.debeEjecutarse(ahora)).toList();
    }
}
