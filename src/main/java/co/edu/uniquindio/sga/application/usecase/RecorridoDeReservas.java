package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CriterioBusquedaReserva;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;

import java.util.ArrayList;
import java.util.List;

/**
 * Los reportes (7.9) necesitan agregar sobre todas las reservas. Como el repositorio solo expone consultas
 * paginadas, se recorren sus páginas una a una en lugar de pedir todo de golpe.
 */
final class RecorridoDeReservas {

    private RecorridoDeReservas() {
    }

    static List<Reserva> todas(ReservaRepository repositorio, CriterioBusquedaReserva criterio) {
        List<Reserva> acumuladas = new ArrayList<>();
        int numero = 0;
        Pagina<Reserva> pagina;
        do {
            pagina = repositorio.buscar(criterio, numero++);
            acumuladas.addAll(pagina.contenido());
        } while (!pagina.esUltima());
        return acumuladas;
    }
}
