package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Recordatorio;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordatorioRepository {

    void guardar(Recordatorio recordatorio);

    /** Todos los recordatorios de una reserva, en cualquier estado (RN-24, RN-25). */
    List<Recordatorio> buscarPorReserva(CodigoReserva reserva);

    /** PROGRAMADOS cuya fecha de envío ya llegó: los consume la tarea programada de envío. */
    List<Recordatorio> buscarPorEjecutar(LocalDateTime ahora);
}
