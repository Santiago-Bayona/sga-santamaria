package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

/** Cantidad de dormitorios de un apartamento; debe ser positiva. */
public record Dormitorios(int cantidad) {
    public Dormitorios {
        if (cantidad <= 0) throw new ReglaDominioException("El apartamento debe tener al menos un dormitorio.");
    }
}
