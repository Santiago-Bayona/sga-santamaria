package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.application.soporte.GeneradorResumenFalso;
import co.edu.uniquindio.sga.application.soporte.ReservaRepositoryPorCriterio;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.soporte.DatosDePrueba;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;
import co.edu.uniquindio.sga.domain.valueobject.Registro;
import co.edu.uniquindio.sga.domain.valueobject.ResumenDiario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Resumen diario con IA no bloqueante (10.2, I-04, I-05). Sin Spring y sin mocks: repositorio y puerto de IA en
 * memoria. Lo que se demuestra: el resumen siempre sale, con o sin IA, y la IA solo recibe lo mínimo.
 */
class GenerarResumenDiarioUseCaseTest {

    private static final LocalDate HOY = LocalDate.of(2026, 9, 10);

    private ReservaRepositoryPorCriterio reservas;
    private Apartamento apartamento;
    private int consecutivo;

    @BeforeEach
    void preparar() {
        reservas = new ReservaRepositoryPorCriterio();
        apartamento = DatosDePrueba.apartamento("APT-101", 4);
        consecutivo = 0;
    }

    private GenerarResumenDiarioUseCase casoDeUso(GeneradorResumenFalso generador) {
        return new GenerarResumenDiarioUseCase(new ConsultarLlegadasPrevistasUseCase(reservas),
                new ConsultarSalidasPrevistasUseCase(reservas), generador);
    }

    /** Reserva CONFIRMADA cuya entrada es HOY, con la hora estimada dada. */
    private Reserva llegadaHoy(String codigo, LocalTime hora) {
        Reserva reserva = DatosDePrueba.reservaPendiente(codigo, apartamento, DatosDePrueba.estancia(HOY, 3));
        reserva.definirHoraLlegadaEstimada(hora);
        reserva.confirmar();
        reservas.guardar(reserva);
        return reserva;
    }

    /** Reserva EN_CURSO cuya salida es HOY. */
    private Reserva salidaHoy(String codigo) {
        Estancia estancia = DatosDePrueba.estancia(HOY.minusDays(3), 3);
        Reserva reserva = DatosDePrueba.reservaPendiente(codigo, apartamento, estancia);
        reserva.confirmar();
        reserva.registrarLlegada(new Registro(estancia.entrada().atTime(16, 0)));
        reservas.guardar(reserva);
        return reserva;
    }

    private String codigo() {
        return String.format("RES-%03d", ++consecutivo);
    }

    @Test
    @DisplayName("10.2: con la IA funcionando, el resumen trae el texto del modelo y las listas del día")
    void conIaFuncionando() {
        llegadaHoy("RES-A", LocalTime.of(15, 0));
        salidaHoy("RES-B");
        GeneradorResumenFalso generador = GeneradorResumenFalso.redactando("Día tranquilo: 1 llegada y 1 salida.");

        ResumenDiario resumen = casoDeUso(generador).ejecutar(HOY);

        assertTrue(resumen.generadoPorIa());
        assertNull(resumen.motivoDegradacion());
        assertEquals("Día tranquilo: 1 llegada y 1 salida.", resumen.texto());
        assertEquals(1, resumen.datos().totalLlegadas());
        assertEquals(1, resumen.datos().totalSalidas());
        assertEquals("RES-A", resumen.datos().llegadas().get(0).reserva().valor());
        assertEquals("RES-B", resumen.datos().salidas().get(0).reserva().valor());
        assertEquals(1, generador.llamadas);
    }

    @Test
    @DisplayName("Solo cuentan las CONFIRMADAS que entran hoy y las EN_CURSO que salen hoy")
    void soloLoPrevistoParaElDia() {
        llegadaHoy("RES-HOY", LocalTime.of(15, 0));
        // Pendiente que entra hoy: todavía no es una llegada prevista
        reservas.guardar(DatosDePrueba.reservaPendiente("RES-PEND", apartamento, DatosDePrueba.estancia(HOY, 2)));
        // Confirmada que entra mañana
        Reserva manana = DatosDePrueba.reservaPendiente("RES-MAN", apartamento,
                DatosDePrueba.estancia(HOY.plusDays(1), 2));
        manana.confirmar();
        reservas.guardar(manana);
        salidaHoy("RES-SAL");
        // EN_CURSO que sale pasado mañana
        Estancia larga = DatosDePrueba.estancia(HOY.minusDays(1), 3);
        Reserva sigueAlojada = DatosDePrueba.reservaPendiente("RES-LARGA", apartamento, larga);
        sigueAlojada.confirmar();
        sigueAlojada.registrarLlegada(new Registro(larga.entrada().atTime(16, 0)));
        reservas.guardar(sigueAlojada);

        ResumenDiario resumen = casoDeUso(GeneradorResumenFalso.redactando("ok")).ejecutar(HOY);

        assertEquals(1, resumen.datos().totalLlegadas());
        assertEquals("RES-HOY", resumen.datos().llegadas().get(0).reserva().valor());
        assertEquals(1, resumen.datos().totalSalidas());
        assertEquals("RES-SAL", resumen.datos().salidas().get(0).reserva().valor());
    }

    @Test
    @DisplayName("Las llegadas salen ordenadas por la hora estimada")
    void llegadasPorHora() {
        llegadaHoy("RES-TARDE", LocalTime.of(18, 0));
        llegadaHoy("RES-TEMPRANO", LocalTime.of(14, 0));

        ResumenDiario resumen = casoDeUso(GeneradorResumenFalso.redactando("ok")).ejecutar(HOY);

        assertEquals("RES-TEMPRANO", resumen.datos().llegadas().get(0).reserva().valor());
        assertEquals("RES-TARDE", resumen.datos().llegadas().get(1).reserva().valor());
    }

    @Test
    @DisplayName("El listado de más de 10 llegadas no se corta en la primera página")
    void recorreTodasLasPaginas() {
        for (int i = 0; i < 12; i++) {
            llegadaHoy(codigo(), LocalTime.of(15, 0));
        }

        ResumenDiario resumen = casoDeUso(GeneradorResumenFalso.redactando("ok")).ejecutar(HOY);

        assertEquals(12, resumen.datos().totalLlegadas());
    }

    @Test
    @DisplayName("10.2: con la IA deshabilitada responde igual, con el resumen básico y el motivo")
    void conIaDeshabilitada() {
        llegadaHoy("RES-A", LocalTime.of(15, 0));
        salidaHoy("RES-B");
        GeneradorResumenFalso generador = GeneradorResumenFalso.noDisponible(MotivoDegradacionIa.IA_DESHABILITADA);

        ResumenDiario resumen = casoDeUso(generador).ejecutar(HOY);

        assertFalse(resumen.generadoPorIa());
        assertEquals(MotivoDegradacionIa.IA_DESHABILITADA, resumen.motivoDegradacion());
        assertTrue(resumen.texto().contains("1 llegada y 1 salida previstas"));
        assertEquals(1, resumen.datos().totalLlegadas());
        assertEquals(1, resumen.datos().totalSalidas());
    }

    @Test
    @DisplayName("I-03, I-04: si el modelo tarda más del tiempo máximo, responde con el resumen básico")
    void tiempoAgotado() {
        llegadaHoy("RES-A", LocalTime.of(15, 0));

        ResumenDiario resumen = casoDeUso(GeneradorResumenFalso.noDisponible(MotivoDegradacionIa.TIEMPO_AGOTADO))
                .ejecutar(HOY);

        assertFalse(resumen.generadoPorIa());
        assertEquals(MotivoDegradacionIa.TIEMPO_AGOTADO, resumen.motivoDegradacion());
        assertEquals(1, resumen.datos().totalLlegadas());
    }

    @Test
    @DisplayName("I-05: un error inesperado del proveedor no rompe el resumen")
    void errorInesperado() {
        llegadaHoy("RES-A", LocalTime.of(15, 0));

        ResumenDiario resumen = casoDeUso(GeneradorResumenFalso.fallandoCon(new IllegalStateException("500 del proveedor")))
                .ejecutar(HOY);

        assertFalse(resumen.generadoPorIa());
        assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR, resumen.motivoDegradacion());
        assertEquals(1, resumen.datos().totalLlegadas());
    }

    @Test
    @DisplayName("Si el generador incumple el contrato y devuelve nulo, se degrada igual")
    void respuestaNula() {
        ResumenDiario resumen = casoDeUso(GeneradorResumenFalso.devolviendoNulo()).ejecutar(HOY);

        assertFalse(resumen.generadoPorIa());
        assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR, resumen.motivoDegradacion());
    }

    @Test
    @DisplayName("I-02: la implementación local simulada se distingue de un modelo real")
    void implementacionSimulada() {
        llegadaHoy("RES-A", LocalTime.of(15, 0));

        ResumenDiario resumen = casoDeUso(GeneradorResumenFalso.simulado("Resumen de plantilla.")).ejecutar(HOY);

        assertFalse(resumen.generadoPorIa());
        assertEquals(MotivoDegradacionIa.IA_SIMULADA, resumen.motivoDegradacion());
        assertEquals("Resumen de plantilla.", resumen.texto());
    }

    @Test
    @DisplayName("Un día sin movimientos también tiene resumen")
    void diaSinMovimientos() {
        GeneradorResumenFalso generador = GeneradorResumenFalso.noDisponible(MotivoDegradacionIa.IA_DESHABILITADA);

        ResumenDiario resumen = casoDeUso(generador).ejecutar(HOY);

        assertEquals(0, resumen.datos().totalLlegadas());
        assertEquals(0, resumen.datos().totalSalidas());
        assertTrue(resumen.texto().contains("0 llegadas y 0 salidas previstas"));
    }

    @Test
    @DisplayName("Al modelo solo le llegan el código, el apartamento, la hora y el número de personas")
    void elModeloRecibeLoMinimo() {
        llegadaHoy("RES-A", LocalTime.of(15, 0));
        GeneradorResumenFalso generador = GeneradorResumenFalso.redactando("ok");

        casoDeUso(generador).ejecutar(HOY);

        var movimiento = generador.ultimosDatos.llegadas().get(0);
        assertEquals("RES-A", movimiento.reserva().valor());
        assertEquals("APT-101", movimiento.apartamento().valor());
        assertEquals(LocalTime.of(15, 0), movimiento.horaEstimada());
        assertEquals(1, movimiento.totalOcupantes());
        // Los campos del movimiento son exactamente estos cuatro: no hay nombre, correo, teléfono ni documento
        assertEquals(4, co.edu.uniquindio.sga.domain.valueobject.MovimientoDiario.class.getRecordComponents().length);
    }

    @Test
    @DisplayName("La fecha del resumen es obligatoria")
    void fechaObligatoria() {
        assertThrows(SolicitudAplicacionException.class,
                () -> casoDeUso(GeneradorResumenFalso.redactando("ok")).ejecutar(null));
    }
}
