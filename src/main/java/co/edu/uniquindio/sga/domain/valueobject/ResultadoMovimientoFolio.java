package co.edu.uniquindio.sga.domain.valueobject;

/**
 * Resultado de un movimiento en el folio: el movimiento creado (con su numero, necesario para revertirlo
 * después), la reserva a la que pertenece ese numero y el saldo resultante.
 * @param <T> Pago o Cargo
 */
public record ResultadoMovimientoFolio<T>(CodigoReserva reserva, T movimiento, Saldo saldo) {
}