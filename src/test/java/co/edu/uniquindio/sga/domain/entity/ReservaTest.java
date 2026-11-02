package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.soporte.DatosDePrueba;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.*;
import static org.junit.jupiter.api.Assertions.*;

/** RN-04 (no reservas en el pasado), RN-08 (transiciones), RN-12 (liberar noches) y 3.2 (todos cuentan para capacidad). */
class ReservaTest {

    private static final LocalDate ENTRADA = LocalDate.of(2026, 9, 10);

    private Apartamento apartamento;
    private Estancia estancia;

    @BeforeEach
    void preparar() {
        apartamento = DatosDePrueba.apartamento("APT-301", 3);
        estancia = estancia(ENTRADA, 3);
    }

    // ---------------- RN-04 ----------------

    @Test
    @DisplayName("RN-04: no se crea una reserva con entrada anterior a hoy")
    void noCrearReservaConEntradaEnElPasado() {
        Estancia ayer = estancia(HOY.minusDays(1), 2);

        assertThrows(ReglaDominioException.class, () -> reservaPendiente("RES-1", apartamento, ayer));
    }

    @Test
    @DisplayName("RN-04: se puede crear con entrada hoy o en el futuro, y nace PENDIENTE")
    void crearReservaConEntradaHoyOEnElFuturo() {
        Reserva hoy = reservaPendiente("RES-1", apartamento, estancia(HOY, 1));
        Reserva futura = reservaPendiente("RES-2", apartamento, estancia, List.of());

        assertEquals(EstadoReserva.PENDIENTE, hoy.getEstado());
        assertEquals(EstadoReserva.PENDIENTE, futura.getEstado());
    }

    @Test
    @DisplayName("RN-04: tampoco se puede modificar una reserva moviéndola al pasado")
    void noModificarReservaHaciaElPasado() {
        Reserva reserva = reservaPendiente("RES-1", apartamento, estancia);
        Estancia pasada = estancia(HOY.minusDays(3), 2);
        Cotizacion cotizacion = cotizar(apartamento, pasada, List.of(titular().comoOcupante()));

        assertThrows(ReglaDominioException.class, () -> reserva.modificar(apartamento.getIdentificacion(),
                apartamento.getCapacidad(), pasada, List.of(), cotizacion, UMBRAL, AHORA));
        assertEquals(estancia, reserva.getEstancia());
    }

    // ---------------- RN-08 ----------------

    @Test
    @DisplayName("RN-08: recorrido válido PENDIENTE → CONFIRMADA → EN_CURSO → FINALIZADA")
    void recorrerElCicloDeVidaPermitido() {
        Reserva reserva = reservaPendiente("RES-1", apartamento, estancia);

        reserva.confirmar();
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());

        reserva.registrarLlegada(new Registro(ENTRADA.atTime(16, 0)));
        assertEquals(EstadoReserva.EN_CURSO, reserva.getEstado());

        reserva.registrarSalida(new Salida(ENTRADA.plusDays(3).atTime(10, 0)));
        assertEquals(EstadoReserva.FINALIZADA, reserva.getEstado());
    }

    @Test
    @DisplayName("RN-08: desde PENDIENTE no se puede registrar la llegada ni la salida")
    void noSaltarEstadosDesdePendiente() {
        Reserva reserva = reservaPendiente("RES-1", apartamento, estancia);

        assertThrows(ReglaDominioException.class,
                () -> reserva.registrarLlegada(new Registro(ENTRADA.atTime(16, 0))));
        assertThrows(ReglaDominioException.class,
                () -> reserva.registrarSalida(new Salida(ENTRADA.plusDays(3).atTime(10, 0))));
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }

    @Test
    @DisplayName("RN-08: una reserva CONFIRMADA no se puede confirmar otra vez")
    void noConfirmarDosVeces() {
        Reserva reserva = reservaConfirmada("RES-1", apartamento, estancia);

        assertThrows(ReglaDominioException.class, reserva::confirmar);
    }

    @Test
    @DisplayName("RN-08: una reserva EN_CURSO ya no se puede cancelar")
    void noCancelarReservaEnCurso() {
        Reserva reserva = reservaEnCurso("RES-1", apartamento, estancia);

        assertThrows(ReglaDominioException.class, () -> reserva.cancelar(new MotivoCancelacion("Ya no viajo")));
        assertEquals(EstadoReserva.EN_CURSO, reserva.getEstado());
    }

    @Test
    @DisplayName("RN-08: los estados terminales (CANCELADA, FINALIZADA) no admiten ninguna transición")
    void noSalirDeUnEstadoTerminal() {
        Reserva cancelada = reservaPendiente("RES-1", apartamento, estancia);
        cancelada.cancelar(new MotivoCancelacion("Cambio de planes"));
        Reserva finalizada = reservaEnCurso("RES-2", apartamento, estancia);
        finalizada.registrarSalida(new Salida(ENTRADA.plusDays(3).atTime(10, 0)));

        assertThrows(ReglaDominioException.class, cancelada::confirmar);
        assertThrows(ReglaDominioException.class, () -> cancelada.cancelar(new MotivoCancelacion("Otra vez")));
        assertThrows(ReglaDominioException.class, () -> finalizada.cancelar(new MotivoCancelacion("Tarde")));
    }

    @Test
    @DisplayName("RN-09 (apoyo de RN-08): no se confirma sin hora estimada de llegada")
    void noConfirmarSinHoraEstimadaDeLlegada() {
        Titular titular = titular();
        Reserva sinHora = Reserva.crear(new CodigoReserva("RES-1"), titular, apartamento.getIdentificacion(),
                apartamento.getCapacidad(), estancia, List.of(), null,
                cotizar(apartamento, estancia, List.of(titular.comoOcupante())), new VersionPolitica(1),
                CanalOrigen.PORTAL, null, UMBRAL, AHORA);

        assertThrows(ReglaDominioException.class, sinHora::confirmar);
    }

    // ---------------- RN-12 ----------------

    @Test
    @DisplayName("RN-12: una reserva activa retiene sus noches")
    void retenerNochesMientrasLaReservaEstaActiva() {
        Reserva reserva = reservaConfirmada("RES-1", apartamento, estancia);

        assertTrue(reserva.estaActiva());
        assertTrue(reserva.ocupa(new Noche(ENTRADA)));
        assertTrue(reserva.ocupa(new Noche(ENTRADA.plusDays(2))));
        assertFalse(reserva.ocupa(new Noche(ENTRADA.plusDays(3))));            // la noche de salida no se ocupa
        assertTrue(reserva.seSolapaCon(estancia(ENTRADA.plusDays(1), 1)));
    }

    @Test
    @DisplayName("RN-12: al cancelarse deja de ser activa y libera todas sus noches")
    void liberarNochesAlCancelarReserva() {
        Reserva reserva = reservaConfirmada("RES-1", apartamento, estancia);

        reserva.cancelar(new MotivoCancelacion("Cambio de planes"));

        assertFalse(reserva.estaActiva());
        for (Noche noche : estancia.noches()) {
            assertFalse(reserva.ocupa(noche));
        }
        assertFalse(reserva.seSolapaCon(estancia));
    }

    @Test
    @DisplayName("RN-12: al finalizar y al declararse no-show también deja de retener noches")
    void liberarNochesAlFinalizarOPorNoShow() {
        Reserva finalizada = reservaEnCurso("RES-1", apartamento, estancia);
        finalizada.registrarSalida(new Salida(ENTRADA.plusDays(3).atTime(10, 0)));
        Reserva noShow = reservaConfirmada("RES-2", apartamento, estancia);
        noShow.declararNoShow(ENTRADA.atTime(19, 0), java.time.LocalTime.of(18, 0));

        assertFalse(finalizada.estaActiva());
        assertEquals(EstadoReserva.NO_SHOW, noShow.getEstado());
        assertFalse(noShow.estaActiva());
        assertFalse(noShow.ocupa(new Noche(ENTRADA)));
    }

    @Test
    @DisplayName("RN-12: cancelar exige un motivo")
    void noCancelarSinMotivo() {
        Reserva reserva = reservaConfirmada("RES-1", apartamento, estancia);

        assertThrows(ReglaDominioException.class, () -> reserva.cancelar(null));
        assertThrows(ReglaDominioException.class, () -> new MotivoCancelacion(" "));
        assertTrue(reserva.estaActiva());
    }

    // ---------------- 3.2 ----------------

    @Test
    @DisplayName("3.2: el titular es siempre un ocupante: grupo = titular + acompañantes")
    void incluirAlTitularEnElTotalDeOcupantes() {
        Reserva reserva = reservaPendiente("RES-1", apartamento, estancia,
                List.of(adulto("Luis"), adulto("Marta")));

        assertEquals(3, reserva.totalOcupantes());
        assertEquals(2, reserva.getAcompanantes().size());
        assertEquals("Ana Gómez", reserva.getOcupantes().get(0).nombre());
    }

    @Test
    @DisplayName("3.2/RN-02: un menor no facturable cuenta para la capacidad y puede impedir la reserva")
    void contarAlMenorNoFacturableParaLaCapacidad() {
        Ocupante nino = conEdadEn("Pedro", 2, ENTRADA);        // 2 años < umbral 5 → no facturable
        Titular titular = titular();
        List<Ocupante> acompanantes = List.of(adulto("Luis"), nino);   // titular + 2 = 3 = capacidad
        Reserva cabe = reservaPendiente("RES-1", apartamento, estancia, acompanantes);
        assertEquals(3, cabe.totalOcupantes());
        assertEquals(2, cabe.ocupantesFacturables(UMBRAL));

        List<Ocupante> demasiados = List.of(adulto("Luis"), nino, conEdadEn("Sara", 1, ENTRADA));  // 4 > 3
        Cotizacion cotizacion = cotizar(apartamento, estancia, List.of(titular.comoOcupante(), adulto("Luis")));
        assertThrows(ReglaDominioException.class, () -> Reserva.crear(new CodigoReserva("RES-2"), titular,
                apartamento.getIdentificacion(), apartamento.getCapacidad(), estancia, demasiados, HORA_LLEGADA,
                cotizacion, new VersionPolitica(1), CanalOrigen.PORTAL, null, UMBRAL, AHORA));
    }

    // ---------------- RN-10 / salida / no-show ----------------

    @Test
    @DisplayName("RN-10: no se registra la llegada antes de la fecha de entrada")
    void noRegistrarLlegadaAntesDeLaFechaDeEntrada() {
        Reserva reserva = reservaConfirmada("RES-1", apartamento, estancia);

        assertThrows(ReglaDominioException.class,
                () -> reserva.registrarLlegada(new Registro(ENTRADA.minusDays(1).atTime(20, 0))));
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        assertThrows(ReglaDominioException.class, () -> reserva.registrarLlegada(null));
    }

    @Test
    @DisplayName("RN-10: la llegada el día de entrada o después es válida y queda registrada")
    void registrarLlegadaElDiaDeEntrada() {
        Reserva reserva = reservaConfirmada("RES-1", apartamento, estancia);

        reserva.registrarLlegada(new Registro(ENTRADA.atTime(16, 30)));

        assertEquals(EstadoReserva.EN_CURSO, reserva.getEstado());
        assertEquals(ENTRADA, reserva.getRegistro().fecha());
    }

    @Test
    @DisplayName("La salida no puede ser anterior al registro de llegada")
    void noRegistrarSalidaAnteriorALaLlegada() {
        Reserva reserva = reservaEnCurso("RES-1", apartamento, estancia);   // llegó a las 16:00

        assertThrows(ReglaDominioException.class,
                () -> reserva.registrarSalida(new Salida(ENTRADA.atTime(9, 0))));
        assertEquals(EstadoReserva.EN_CURSO, reserva.getEstado());
    }

    @Test
    @DisplayName("No-show: solo desde la hora límite del día de entrada y solo sobre reservas CONFIRMADAS")
    void declararNoShowSoloDesdeLaHoraLimite() {
        Reserva confirmada = reservaConfirmada("RES-1", apartamento, estancia);
        Reserva pendiente = reservaPendiente("RES-2", apartamento, estancia);
        java.time.LocalTime limite = java.time.LocalTime.of(18, 0);

        assertThrows(ReglaDominioException.class, () -> confirmada.declararNoShow(ENTRADA.atTime(17, 59), limite));
        assertThrows(ReglaDominioException.class, () -> pendiente.declararNoShow(ENTRADA.atTime(19, 0), limite));
        confirmada.declararNoShow(ENTRADA.atTime(18, 0), limite);
        assertEquals(EstadoReserva.NO_SHOW, confirmada.getEstado());
    }

    // ---------------- RN-14 / RN-22 ----------------

    @Test
    @DisplayName("RN-14: modificar reemplaza fechas y valor, devuelve la diferencia y conserva la política congelada")
    void modificarReservaDevuelveLaDiferenciaYConservaLaPolitica() {
        Reserva reserva = reservaPendiente("RES-1", apartamento, estancia);          // 3 noches × 100.000
        Estancia mas = estancia(ENTRADA, 4);
        Cotizacion nueva = cotizar(apartamento, mas, List.of(titular().comoOcupante()));

        java.math.BigDecimal diferencia = reserva.modificar(apartamento.getIdentificacion(),
                apartamento.getCapacidad(), mas, List.of(), nueva, UMBRAL, AHORA);

        assertEquals(100_000, diferencia.intValue());
        assertEquals(mas, reserva.getEstancia());
        assertEquals(Dinero.pesos(400_000), reserva.getValorCongelado());
        assertEquals(new VersionPolitica(1), reserva.getPoliticaVersionCongelada());   // RN-22
    }

    @Test
    @DisplayName("RN-14: solo se modifica una reserva PENDIENTE o CONFIRMADA")
    void noModificarReservaEnCursoNiCancelada() {
        Reserva enCurso = reservaEnCurso("RES-1", apartamento, estancia);
        Reserva cancelada = reservaPendiente("RES-2", apartamento, estancia);
        cancelada.cancelar(new MotivoCancelacion("Cambio"));
        Cotizacion cotizacion = cotizar(apartamento, estancia, List.of(titular().comoOcupante()));

        assertThrows(ReglaDominioException.class, () -> enCurso.modificar(apartamento.getIdentificacion(),
                apartamento.getCapacidad(), estancia, List.of(), cotizacion, UMBRAL, AHORA));
        assertThrows(ReglaDominioException.class, () -> cancelada.modificar(apartamento.getIdentificacion(),
                apartamento.getCapacidad(), estancia, List.of(), cotizacion, UMBRAL, AHORA));
    }

    @Test
    @DisplayName("RN-14/RN-02: al modificar se vuelven a validar capacidad y cobertura de la cotización")
    void modificarRevalidaCapacidadYCotizacion() {
        Reserva reserva = reservaPendiente("RES-1", apartamento, estancia);
        Cotizacion cotizacionDeTresNoches = cotizar(apartamento, estancia, List.of(titular().comoOcupante()));
        List<Ocupante> cuatro = List.of(adulto("A"), adulto("B"), adulto("C"));          // titular + 3 = 4 > 3

        assertThrows(ReglaDominioException.class, () -> reserva.modificar(apartamento.getIdentificacion(),
                apartamento.getCapacidad(), estancia, cuatro, cotizacionDeTresNoches, UMBRAL, AHORA));
        assertThrows(ReglaDominioException.class, () -> reserva.modificar(apartamento.getIdentificacion(),
                apartamento.getCapacidad(), estancia(ENTRADA, 5), List.of(), cotizacionDeTresNoches, UMBRAL, AHORA));
        assertEquals(estancia, reserva.getEstancia());
    }

    // ---------------- Canal externo (base de RN-19) / L-16 ----------------

    @Test
    @DisplayName("Canal externo (base de RN-19): solo las reservas de canal externo llevan identificador externo, y todas deben llevarlo")
    void exigirIdentificadorExternoSoloEnCanalExterno() {
        Titular titular = titular();
        Cotizacion cotizacion = cotizar(apartamento, estancia, List.of(titular.comoOcupante()));

        assertThrows(ReglaDominioException.class, () -> crearConCanal(titular, cotizacion, CanalOrigen.EXTERNO, null));
        assertThrows(ReglaDominioException.class,
                () -> crearConCanal(titular, cotizacion, CanalOrigen.PORTAL, new IdentificadorExterno("BKG-1")));
        Reserva externa = crearConCanal(titular, cotizacion, CanalOrigen.EXTERNO, new IdentificadorExterno("BKG-1"));
        assertEquals(new IdentificadorExterno("BKG-1"), externa.getIdentificadorExterno());
    }

    private Reserva crearConCanal(Titular titular, Cotizacion cotizacion, CanalOrigen canal, IdentificadorExterno id) {
        return Reserva.crear(new CodigoReserva("RES-X"), titular, apartamento.getIdentificacion(),
                apartamento.getCapacidad(), estancia, List.of(), HORA_LLEGADA, cotizacion, new VersionPolitica(1),
                canal, id, UMBRAL, AHORA);
    }

    @Test
    @DisplayName("L-16: máximo 2 mascotas por reserva")
    void noAdmitirMasDeDosMascotas() {
        Titular titular = titular();
        Cotizacion cotizacion = cotizar(apartamento, estancia, List.of(titular.comoOcupante()));

        Reserva conDos = Reserva.crear(new CodigoReserva("RES-M"), titular, apartamento.getIdentificacion(),
                apartamento.getCapacidad(), estancia, List.of(), new Mascotas(2), HORA_LLEGADA, cotizacion,
                new VersionPolitica(1), CanalOrigen.PORTAL, null, UMBRAL, AHORA);

        assertEquals(2, conDos.getMascotas().cantidad());
        assertThrows(ReglaDominioException.class, () -> new Mascotas(3));
        assertThrows(ReglaDominioException.class, () -> new Mascotas(-1));
    }

    // ---------------- 3.2: el titular debe ser facturable ----------------

    @Test
    @DisplayName("3.2: el titular debe ser un ocupante facturable")
    void noPermitirTitularMenorSinCargo() {
        Titular bebe = new Titular(new CodigoTitular("TIT-B"), "Bebé", ENTRADA.minusYears(2),
                "3000000000", "bebe@correo.com");
        Cotizacion cotizacion = cotizar(apartamento, estancia, List.of(adulto("Luis")));

        assertThrows(ReglaDominioException.class, () -> Reserva.crear(new CodigoReserva("RES-B"), bebe,
                apartamento.getIdentificacion(), apartamento.getCapacidad(), estancia, List.of(adulto("Luis")),
                HORA_LLEGADA, cotizacion, new VersionPolitica(1), CanalOrigen.PORTAL, null, UMBRAL, AHORA));
    }
}
