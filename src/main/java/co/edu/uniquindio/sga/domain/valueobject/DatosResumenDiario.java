package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;
import java.util.List;

/**
 * Lo que se entrega al generador de resumen: las llegadas y salidas previstas de un día (ficha A.6).
 * Es el único dato que cruza el puerto hacia la IA.
 */

public record DatosResumenDiario(LocalDate fecha, List<MovimientoDiario> llegadas, List<MovimientoDiario> salidas) {

    public DatosResumenDiario {
        if (fecha == null) {
            throw new ReglaDominioException("El resumen requiere la fecha del día.");
        }
        if (llegadas == null || salidas == null) {
            throw new ReglaDominioException("El resumen requiere las listas de llegadas y de salidas.");
        }
        llegadas = List.copyOf(llegadas);
        salidas = List.copyOf(salidas);
    }

    public int totalLlegadas() {
        return llegadas.size();
    }

    public int totalSalidas() {
        return salidas.size();
    }
}
