package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.util.List;
import java.util.function.Function;

/**
 * Página de resultados de un puerto de repositorio. Tipo del dominio, sin dependencias de Spring.
 * Las páginas se numeran desde 0 y tienen {@link #TAMANO} elementos (12.4).
 *
 * @param contenido elementos de esta página
 * @param numero    número de página, desde 0
 * @param total     total de elementos que cumplen el criterio (todas las páginas)
 */
public record Pagina<T>(List<T> contenido, int numero, long total) {

    public static final int TAMANO = 10;

    public Pagina {
        if (contenido == null) {
            throw new ReglaDominioException("El contenido de la página es obligatorio.");
        }
        if (numero < 0) {
            throw new ReglaDominioException("El número de página no puede ser negativo.");
        }
        if (total < 0) {
            throw new ReglaDominioException("El total de elementos no puede ser negativo.");
        }
        contenido = List.copyOf(contenido);
    }

    public int totalPaginas() {
        return (int) ((total + TAMANO - 1) / TAMANO);
    }

    public boolean esUltima() {
        return numero + 1 >= totalPaginas();
    }

    public <R> Pagina<R> mapear(Function<? super T, ? extends R> conversion) {
        return new Pagina<>(contenido.stream().<R>map(conversion).toList(), numero, total);
    }
}
