package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Periodo de un reporte (7.9), con ambas fechas incluidas: del 1 al 30 son 30 noches.
 * Es distinto de Estancia, cuyo intervalo es [entrada, salida) según 3.1.
 */
public record PeriodoReporte(LocalDate desde, LocalDate hasta) {

    public PeriodoReporte {
        if (desde == null || hasta == null) {
            throw new ReglaDominioException("El periodo del reporte requiere fecha inicial y final.");
        }
        if (desde.isAfter(hasta)) {
            throw new ReglaDominioException("La fecha inicial del reporte no puede ser posterior a la final.");
        }
    }

    /** Noches del periodo: días calendario entre desde y hasta, ambos incluidos. */
    public long noches() {
        return ChronoUnit.DAYS.between(desde, hasta) + 1;
    }

    public boolean contiene(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    /** Noches de la estancia [entrada, salida) que caen dentro del periodo. */
    public long nochesDe(Estancia estancia) {
        LocalDate primera = estancia.entrada().isAfter(desde) ? estancia.entrada() : desde;
        LocalDate ultimaNoche = estancia.salida().minusDays(1);
        LocalDate ultima = ultimaNoche.isBefore(hasta) ? ultimaNoche : hasta;
        return Math.max(0, ChronoUnit.DAYS.between(primera, ultima) + 1);
    }
}
