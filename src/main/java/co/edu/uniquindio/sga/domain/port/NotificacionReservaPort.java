package co.edu.uniquindio.sga.domain.port;

import co.edu.uniquindio.sga.domain.valueobject.AvisoReserva;

/**
 * Puerto de salida (I-01): avisa al titular cuando su reserva se crea, se confirma o se cancela (7.5, 7.8).
 *
 * Contrato de las implementaciones:
 *
 *   No lanzan excepciones: un fallo del envío se registra y la operación de la reserva sigue (no bloqueante).
 *   Solo envían si la transacción de la reserva se confirmó; si se deshace, no sale ningún aviso.
 *
 */
public interface NotificacionReservaPort {

    void notificar(AvisoReserva aviso);
}
