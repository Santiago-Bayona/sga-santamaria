package co.edu.uniquindio.sga.application.dto.response;

import java.util.List;
import java.util.function.Function;

/**
 * DTO de salida de la API. Sobre genérico de TODOS los listados: 10 por página (12.4), páginas desde 0 (guía 07).
 * Genérico y sin tipos del dominio: sirve para cualquier response. Como tus casos de uso hoy devuelven List, el
 * controller puede paginar con PaginaResponse.de(lista, pagina) sin tocar el caso de uso.
 */
public record PaginaResponse<T>(

        List<T> contenido,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas
) {

    public static final int TAMANO_PAGINA = 10;

    public PaginaResponse {
        contenido = List.copyOf(contenido);
    }

    public static <T> PaginaResponse<T> de(List<T> todos, int pagina) {
        if (pagina < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo.");
        }
        int total = todos.size();
        int desde = (int) Math.min((long) pagina * TAMANO_PAGINA, total);
        int hasta = Math.min(desde + TAMANO_PAGINA, total);
        return new PaginaResponse<>(todos.subList(desde, hasta), pagina, TAMANO_PAGINA, total,
                (total + TAMANO_PAGINA - 1) / TAMANO_PAGINA);
    }

    /** Para listados que ya vienen paginados desde el repositorio: solo recibe el contenido de la página y el total. */
    public static <T> PaginaResponse<T> desde(List<T> contenido, int pagina, long totalElementos) {
        return new PaginaResponse<>(contenido, pagina, TAMANO_PAGINA, totalElementos,
                (int) ((totalElementos + TAMANO_PAGINA - 1) / TAMANO_PAGINA));
    }

    public <R> PaginaResponse<R> mapear(Function<? super T, ? extends R> conversion) {
        return new PaginaResponse<>(contenido.stream().<R>map(conversion).toList(), pagina, tamano, totalElementos, totalPaginas);
    }
}
