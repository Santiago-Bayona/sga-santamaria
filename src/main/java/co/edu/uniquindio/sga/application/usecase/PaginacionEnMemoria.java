package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;

import java.util.List;

/**
 * Pagina una lista ya cargada, con el mismo tamaño y numeración que los puertos de repositorio (10 por página,
 * desde 0). Se usa solo para listados cuyo puerto todavía no expone una consulta paginada: el contenido es pequeño
 * (catálogos, o un único agregado). Cuando el puerto gane {@code Pagina<T> buscar(..., int pagina)} basta con
 * delegar en él y borrar esta llamada.
 */
final class PaginacionEnMemoria {

    private PaginacionEnMemoria() {
    }

    static <T> Pagina<T> de(List<T> todos, int pagina) {
        if (pagina < 0) {
            throw new SolicitudAplicacionException("El número de página no puede ser negativo.");
        }
        int desde = (int) Math.min((long) pagina * Pagina.TAMANO, todos.size());
        int hasta = Math.min(desde + Pagina.TAMANO, todos.size());
        return new Pagina<>(todos.subList(desde, hasta), pagina, todos.size());
    }
}
