package co.edu.uniquindio.sga.domain.exception;

import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;

/**
 * El generador de resumen no pudo redactar: está apagado, tardó demasiado o el proveedor falló. No es una regla de
 * negocio rota (no extiende {@link ReglaDominioException}).
 */

public class GeneradorResumenNoDisponibleException extends RuntimeException {

    private final MotivoDegradacionIa motivo;

    public GeneradorResumenNoDisponibleException(MotivoDegradacionIa motivo, String mensaje) {
        super(mensaje);
        this.motivo = motivo;
    }

    public GeneradorResumenNoDisponibleException(MotivoDegradacionIa motivo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.motivo = motivo;
    }

    public MotivoDegradacionIa getMotivo() {
        return motivo;
    }
}
