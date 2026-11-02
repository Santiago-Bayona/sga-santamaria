package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.valueobject.EstadoOperativo;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;

import java.util.List;
import java.util.Optional;

public interface ApartamentoRepository {

    Optional<Apartamento> obtenerPorIdentificacion(IdentificacionApartamento identificacion);

    void guardar(Apartamento apartamento);

    /** Apartamentos no retirados de la venta. */
    List<Apartamento> buscarActivos();

    /** Listado paginado de apartamentos activos (orden por identificación). */
    Pagina<Apartamento> buscarActivos(int pagina);

    /** 7.1 personal de servicio: p. ej. los apartamentos PENDIENTE_PREPARACION. */
    List<Apartamento> buscarPorEstadoOperativo(EstadoOperativo estado);
}
