package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.EventoBitacoraCanal;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;

public interface EventoBitacoraCanalRepository {

    void guardar(EventoBitacoraCanal evento);

    /** Más reciente primero, páginas de {@link Pagina#TAMANO}. */
    Pagina<EventoBitacoraCanal> listar(int pagina);
}
