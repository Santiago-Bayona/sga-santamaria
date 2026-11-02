package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.IndicadorIngresos;
import co.edu.uniquindio.sga.domain.valueobject.IndicadorOcupacion;
import co.edu.uniquindio.sga.domain.valueobject.MedioPago;
import co.edu.uniquindio.sga.domain.valueobject.Pago;
import co.edu.uniquindio.sga.domain.valueobject.PeriodoReporte;
import co.edu.uniquindio.sga.domain.valueobject.ReservasDeCanal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reportes e indicadores (7.9). Cada indicador se calcula sobre muchas reservas, folios y apartamentos a la vez,
 * así que ningún agregado puede calcularlo solo. Recibe los datos ya cargados: no usa repositorios ni Spring.
 */
public class IndicadoresService {

    /** Noche vendida = noche de una reserva que llegó a confirmarse y no se cayó (ni PENDIENTE, ni CANCELADA, ni NO_SHOW). */
    private static final Set<EstadoReserva> ESTADOS_VENDIDOS =
            EnumSet.of(EstadoReserva.CONFIRMADA, EstadoReserva.EN_CURSO, EstadoReserva.FINALIZADA);

    /**
     * Ocupación: noches vendidas dentro del periodo / (apartamentos activos × noches del periodo) × 100,
     * con dos decimales. Sin apartamentos activos la ocupación es 0.
     */
    public IndicadorOcupacion ocupacion(List<Reserva> reservas, int apartamentosActivos, PeriodoReporte periodo) {
        exigir(reservas, periodo);
        if (apartamentosActivos < 0) {
            throw new ReglaDominioException("La cantidad de apartamentos activos no puede ser negativa.");
        }
        long nochesVendidas = reservas.stream()
                .filter(reserva -> ESTADOS_VENDIDOS.contains(reserva.getEstado()))
                .mapToLong(reserva -> periodo.nochesDe(reserva.getEstancia()))
                .sum();
        long nochesDisponibles = apartamentosActivos * periodo.noches();
        BigDecimal porcentaje = nochesDisponibles == 0
                ? BigDecimal.ZERO.setScale(2)
                : BigDecimal.valueOf(nochesVendidas).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(nochesDisponibles), 2, RoundingMode.HALF_UP);
        return new IndicadorOcupacion(periodo, nochesVendidas, apartamentosActivos, periodo.noches(), porcentaje);
    }

    /**
     * Ingresos: suma de los pagos registrados en el periodo, por medio de pago. Los cargos no pagados no cuentan.
     * Siempre trae los tres medios, aunque alguno quede en cero.
     */
    public IndicadorIngresos ingresos(List<Folio> folios, PeriodoReporte periodo) {
        exigir(folios, periodo);
        Map<MedioPago, BigDecimal> porMedio = new EnumMap<>(MedioPago.class);
        Arrays.stream(MedioPago.values()).forEach(medio -> porMedio.put(medio, BigDecimal.ZERO));
        for (Folio folio : folios) {
            for (Pago pago : folio.getPagos()) {
                if (periodo.contiene(pago.fecha())) {
                    porMedio.merge(pago.medio(), pago.valorConSigno(), BigDecimal::add);
                }
            }
        }
        BigDecimal total = porMedio.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return new IndicadorIngresos(periodo, porMedio, total);
    }

    /**
     * Reservas por canal de origen, según su fecha de creación, incluyendo canceladas y no-show.
     * Siempre trae los tres canales, aunque alguno tenga cero.
     */
    public List<ReservasDeCanal> reservasPorCanal(List<Reserva> reservas, PeriodoReporte periodo) {
        exigir(reservas, periodo);
        List<Reserva> delPeriodo = reservas.stream()
                .filter(reserva -> periodo.contiene(reserva.getFechaCreacion().toLocalDate()))
                .toList();
        return Arrays.stream(CanalOrigen.values())
                .map(canal -> {
                    List<Reserva> delCanal = delPeriodo.stream().filter(r -> r.getCanalOrigen() == canal).toList();
                    return new ReservasDeCanal(canal, delCanal.size(),
                            delCanal.stream().filter(r -> r.getEstado() == EstadoReserva.CANCELADA).count(),
                            delCanal.stream().filter(r -> r.getEstado() == EstadoReserva.NO_SHOW).count());
                })
                .toList();
    }

    private static void exigir(List<?> datos, PeriodoReporte periodo) {
        if (datos == null || periodo == null) {
            throw new ReglaDominioException("El reporte requiere los datos y el periodo.");
        }
    }
}
