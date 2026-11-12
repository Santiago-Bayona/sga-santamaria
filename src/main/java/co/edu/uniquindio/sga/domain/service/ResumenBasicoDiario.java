package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.MovimientoDiario;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resumen diario armado por el sistema, sin IA. Es el que se muestra cuando la IA está apagada o falla (10.2), y
 * también lo usa la implementación local simulada. Mismo texto para los mismos datos.
 */

public final class ResumenBasicoDiario {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ResumenBasicoDiario() {
    }

    public static String redactar(DatosResumenDiario datos) {
        StringBuilder texto = new StringBuilder();
        texto.append("Resumen del ").append(datos.fecha().format(FECHA)).append(": ")
                .append(cantidad(datos.totalLlegadas(), "llegada")).append(" y ")
                .append(cantidad(datos.totalSalidas(), "salida")).append(" previstas.");
        if (!datos.llegadas().isEmpty()) {
            texto.append("\nLlegadas: ").append(listar(datos.llegadas(), true)).append('.');
        }
        if (!datos.salidas().isEmpty()) {
            texto.append("\nSalidas: ").append(listar(datos.salidas(), false)).append('.');
        }
        return texto.toString();
    }

    private static String cantidad(int total, String sustantivo) {
        return total + " " + sustantivo + (total == 1 ? "" : "s");
    }

    private static String listar(List<MovimientoDiario> movimientos, boolean conHora) {
        return movimientos.stream().map(m -> detalle(m, conHora)).collect(Collectors.joining("; "));
    }

    private static String detalle(MovimientoDiario movimiento, boolean conHora) {
        StringBuilder detalle = new StringBuilder();
        detalle.append(movimiento.reserva().valor()).append(" (").append(movimiento.apartamento().valor())
                .append(", ").append(movimiento.totalOcupantes())
                .append(movimiento.totalOcupantes() == 1 ? " persona" : " personas");
        if (conHora && movimiento.horaEstimada() != null) {
            detalle.append(", hacia las ").append(movimiento.horaEstimada());
        }
        return detalle.append(')').toString();
    }
}
