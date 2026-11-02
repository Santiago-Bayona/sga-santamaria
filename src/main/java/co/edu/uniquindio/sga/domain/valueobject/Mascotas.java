package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

/**
 * Cantidad de mascotas que acompañan una reserva (L-16). Máximo {@value #MAXIMO_POR_APARTAMENTO} por apartamento.
 * El cargo por mascotas es único por estancia y su valor sale del servicio adicional configurado en el
 * alojamiento con el nombre {@link #SERVICIO_ADICIONAL}.
 */
public record Mascotas(int cantidad) {

    public static final int MAXIMO_POR_APARTAMENTO = 2;
    public static final String SERVICIO_ADICIONAL = "Mascotas";
    public static final Mascotas NINGUNA = new Mascotas(0);

    public Mascotas {
        if (cantidad < 0) {
            throw new ReglaDominioException("La cantidad de mascotas no puede ser negativa.");
        }
        if (cantidad > MAXIMO_POR_APARTAMENTO) {
            throw new ReglaDominioException("Se permiten máximo " + MAXIMO_POR_APARTAMENTO
                    + " mascotas por apartamento.");
        }
    }

    public boolean hay() {
        return cantidad > 0;
    }
}
