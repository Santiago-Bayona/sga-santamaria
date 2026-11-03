package co.edu.uniquindio.sga.domain.repository;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.valueobject.*;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del dominio: describe QUÉ necesita el dominio, no CÓMO se implementa.
 *  - obtenerPorX : recupera un elemento por su identidad (devuelve Optional)
 *  - buscarPorX  : recupera una colección según un criterio
 */
public interface ReservaRepository {

    Optional<Reserva> obtenerPorCodigo(CodigoReserva codigo);

    void guardar(Reserva reserva);

    List<Reserva> buscarPorEstado(EstadoReserva estado);

    /** Reservas PENDIENTE, CONFIRMADA o EN_CURSO del apartamento: las que cuentan para RN-01 y RN-20. */
    List<Reserva> buscarActivasPorApartamento(IdentificacionApartamento apartamento);

    /** RN-19: un mismo mensaje externo no crea dos reservas. */
    Optional<Reserva> obtenerPorIdentificadorExterno(CanalOrigen canal, IdentificadorExterno identificadorExterno);

    /** Todas las reservas de un titular, en cualquier estado (historial del huésped, 7.6). */
    List<Reserva> buscarPorTitular(CodigoTitular titular);

    /**
     * Listado con filtros y paginación (7.5): más reciente primero, {@link Pagina#TAMANO} por página,
     * páginas desde 0. Reemplaza a la antigua consulta sin límite de todas las reservas.
     */
    Pagina<Reserva> buscar(CriterioBusquedaReserva criterio, int pagina);
}
