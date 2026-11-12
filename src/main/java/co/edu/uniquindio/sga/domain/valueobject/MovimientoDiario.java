package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalTime;

/**
 * Una llegada o una salida prevista, reducida a lo mínimo para planear el día. A propósito no lleva nombres,
 * correos, teléfonos ni documentos: es lo único que sale hacia el modelo de IA.
 *
 * @param reserva        código de la reserva
 * @param apartamento    apartamento que ocupa
 * @param horaEstimada   hora estimada de llegada; nula en las salidas, que no tienen hora estimada
 * @param totalOcupantes personas del grupo
 */

public record MovimientoDiario(
        CodigoReserva reserva,
        IdentificacionApartamento apartamento,
        LocalTime horaEstimada,
        int totalOcupantes
) {

    public MovimientoDiario {
        if (reserva == null) {
            throw new ReglaDominioException("El movimiento requiere el código de la reserva.");
        }
        if (apartamento == null) {
            throw new ReglaDominioException("El movimiento requiere el apartamento.");
        }
        if (totalOcupantes < 1) {
            throw new ReglaDominioException("El movimiento requiere al menos un ocupante.");
        }
    }

    /** Movimiento de una llegada: lleva la hora estimada de llegada de la reserva. */
    public static MovimientoDiario deLlegada(Reserva reserva) {
        return new MovimientoDiario(reserva.getCodigo(), reserva.getApartamento(),
                reserva.getHoraLlegadaEstimada(), reserva.totalOcupantes());
    }

    /** Movimiento de una salida: sin hora estimada. */
    public static MovimientoDiario deSalida(Reserva reserva) {
        return new MovimientoDiario(reserva.getCodigo(), reserva.getApartamento(), null, reserva.totalOcupantes());
    }
}
