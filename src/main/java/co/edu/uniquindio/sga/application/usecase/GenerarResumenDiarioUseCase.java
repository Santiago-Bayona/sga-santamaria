package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.GeneradorResumenNoDisponibleException;
import co.edu.uniquindio.sga.domain.port.GeneradorResumenPort;
import co.edu.uniquindio.sga.domain.service.ResumenBasicoDiario;
import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;
import co.edu.uniquindio.sga.domain.valueobject.MovimientoDiario;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import co.edu.uniquindio.sga.domain.valueobject.ResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.ResumenRedactado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Resumen diario de llegadas y salidas previstas (ficha A.6, 10.2). Junta las llegadas y salidas del día con los
 * casos de uso que ya existen y pide al puerto de IA que lo redacte.
 *
 * <p>La IA nunca bloquea: si está apagada, tarda, falla o devuelve algo inservible, el caso de uso responde igual
 * con el resumen básico y el motivo (I-04, I-05). Se registra el fallo en el log para revisarlo después.
 *
 * <p>A propósito no es transaccional: las consultas abren sus propias transacciones de lectura y se cierran antes
 * de llamar al proveedor, para no retener una conexión de base de datos mientras se espera a la IA.
 */
@Service
public class GenerarResumenDiarioUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(GenerarResumenDiarioUseCase.class);

    private final ConsultarLlegadasPrevistasUseCase llegadasPrevistas;
    private final ConsultarSalidasPrevistasUseCase salidasPrevistas;
    private final GeneradorResumenPort generador;

    public GenerarResumenDiarioUseCase(ConsultarLlegadasPrevistasUseCase llegadasPrevistas,
                                       ConsultarSalidasPrevistasUseCase salidasPrevistas,
                                       GeneradorResumenPort generador) {
        this.llegadasPrevistas = llegadasPrevistas;
        this.salidasPrevistas = salidasPrevistas;
        this.generador = generador;
    }

    public ResumenDiario ejecutar(LocalDate fecha) {
        if (fecha == null) {
            throw new SolicitudAplicacionException("La fecha del resumen es obligatoria.");
        }
        DatosResumenDiario datos = new DatosResumenDiario(fecha,
                ordenadasPorHora(todas(pagina -> llegadasPrevistas.ejecutar(fecha, pagina), MovimientoDiario::deLlegada)),
                todas(pagina -> salidasPrevistas.ejecutar(fecha, pagina), MovimientoDiario::deSalida));
        return redactar(datos);
    }

    private ResumenDiario redactar(DatosResumenDiario datos) {
        try {
            ResumenRedactado redactado = generador.generar(datos);
            if (redactado == null) {
                throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.ERROR_PROVEEDOR,
                        "El generador no devolvió resumen.");
            }
            if (redactado.generadoPorModelo()) {
                return new ResumenDiario(datos, redactado.texto(), true, null);
            }
            return new ResumenDiario(datos, redactado.texto(), false, MotivoDegradacionIa.IA_SIMULADA);
        } catch (GeneradorResumenNoDisponibleException e) {
            registrar(e.getMotivo(), e);
            return basico(datos, e.getMotivo());
        } catch (RuntimeException e) {
            registrar(MotivoDegradacionIa.ERROR_PROVEEDOR, e);
            return basico(datos, MotivoDegradacionIa.ERROR_PROVEEDOR);
        }
    }

    private ResumenDiario basico(DatosResumenDiario datos, MotivoDegradacionIa motivo) {
        return new ResumenDiario(datos, ResumenBasicoDiario.redactar(datos), false, motivo);
    }

    /** Una IA apagada es una decisión de configuración, no un fallo: no se registra como advertencia. */
    private void registrar(MotivoDegradacionIa motivo, RuntimeException causa) {
        if (motivo == MotivoDegradacionIa.IA_DESHABILITADA) {
            LOG.debug("Resumen diario sin IA: {}", motivo);
        } else {
            LOG.warn("Resumen diario sin IA ({}): {}", motivo, causa.getMessage());
        }
    }

    /** Recorre todas las páginas del listado: el resumen necesita todas las llegadas o salidas del día. */
    private static List<MovimientoDiario> todas(Function<Integer, Pagina<Reserva>> consulta,
                                                Function<Reserva, MovimientoDiario> aMovimiento) {
        Stream.Builder<MovimientoDiario> movimientos = Stream.builder();
        int pagina = 0;
        Pagina<Reserva> actual;
        do {
            actual = consulta.apply(pagina++);
            actual.contenido().stream().map(aMovimiento).forEach(movimientos::add);
        } while (!actual.esUltima());
        return movimientos.build().toList();
    }

    /** Llegadas por hora estimada; las que no tienen hora van al final. */
    private static List<MovimientoDiario> ordenadasPorHora(List<MovimientoDiario> llegadas) {
        return llegadas.stream()
                .sorted(Comparator.comparing(MovimientoDiario::horaEstimada,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }
}
