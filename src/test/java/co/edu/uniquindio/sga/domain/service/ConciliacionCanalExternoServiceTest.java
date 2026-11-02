package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.entity.ConflictoCanal;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService.ResultadoConciliacion;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService.TipoResultado;
import co.edu.uniquindio.sga.domain.soporte.*;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** RN-18 (conflicto: se rechaza y se registra sin sobrescribir) y RN-19 (canal + identificador único, idempotente). */
class ConciliacionCanalExternoServiceTest {

    private static final CodigoCanalExterno CANAL = new CodigoCanalExterno("BOOKING");
    private static final IdentificadorExterno ID_1 = new IdentificadorExterno("BK-1001");
    private static final IdentificadorExterno ID_2 = new IdentificadorExterno("BK-1002");

    private EscenarioReservas e;
    private CanalExterno canal;
    private ConflictoCanalRepositoryEnMemoria conflictos;
    private ConciliacionCanalExternoService servicio;

    @BeforeEach
    void preparar() {
        e = new EscenarioReservas();
        canal = new CanalExterno(CANAL, "OTA", "secreto-de-prueba");
        conflictos = new ConflictoCanalRepositoryEnMemoria();
        servicio = new ConciliacionCanalExternoService(e.reservas, conflictos,
                new CanalExternoRepositoryEnMemoria().con(canal), e.creacion, e.cancelacion);
    }

    private ResultadoConciliacion conciliar(IdentificadorExterno id, Estancia estancia) {
        return servicio.conciliar(CANAL, id, DatosDePrueba.titular(), EscenarioReservas.APTO, estancia,
                List.of(), LocalTime.of(15, 0), DatosDePrueba.AHORA);
    }

    // ---- RN-19 ----

    @Test
    @DisplayName("RN-19: una reserva externa nueva se acepta y queda con canal e identificador")
    void aceptaReservaNueva() {
        ResultadoConciliacion r = conciliar(ID_1, e.estancia());
        assertEquals(TipoResultado.ACEPTADA, r.tipo());
        assertEquals(CanalOrigen.EXTERNO, r.reserva().getCanalOrigen());
        assertEquals(ID_1, r.reserva().getIdentificadorExterno());
    }

    @Test
    @DisplayName("RN-19: el mismo mensaje dos veces no crea una segunda reserva")
    void idempotenteConElMismoIdentificador() {
        ResultadoConciliacion primera = conciliar(ID_1, e.estancia());
        ResultadoConciliacion segunda = conciliar(ID_1, e.estancia());
        assertEquals(TipoResultado.DUPLICADA, segunda.tipo());
        assertSame(primera.reserva(), segunda.reserva());
        assertEquals(1, e.reservas.buscarPorEstado(EstadoReserva.PENDIENTE).size());
    }

    @Test
    @DisplayName("RN-19: reenviar el identificador con otras fechas sigue siendo duplicado (no modifica ni duplica)")
    void duplicadoIgnoraLosDatosNuevos() {
        ResultadoConciliacion primera = conciliar(ID_1, e.estancia());
        ResultadoConciliacion segunda = conciliar(ID_1, DatosDePrueba.estancia(LocalDate.of(2026, 11, 1), 2));
        assertEquals(TipoResultado.DUPLICADA, segunda.tipo());
        assertEquals(primera.reserva().getEstancia(), segunda.reserva().getEstancia());
    }

    @Test
    @DisplayName("RN-19: identificadores distintos son reservas distintas")
    void identificadoresDistintos() {
        conciliar(ID_1, e.estancia());
        ResultadoConciliacion otra = conciliar(ID_2, DatosDePrueba.estancia(LocalDate.of(2026, 11, 1), 2));
        assertEquals(TipoResultado.ACEPTADA, otra.tipo());
        assertEquals(2, e.reservas.buscarPorEstado(EstadoReserva.PENDIENTE).size());
    }

    @Test
    @DisplayName("RN-19: toda reserva externa exige su identificador")
    void exigeIdentificador() {
        assertThrows(ReglaDominioException.class, () -> conciliar(null, e.estancia()));
    }

    @Test
    @DisplayName("RN-19: el identificador externo no puede ser vacío")
    void identificadorNoVacio() {
        assertThrows(ReglaDominioException.class, () -> new IdentificadorExterno("  "));
    }

    @Test
    @DisplayName("RN-19: el canal debe existir y estar activo")
    void canalExistenteYActivo() {
        assertThrows(ReglaDominioException.class, () -> servicio.conciliar(new CodigoCanalExterno("OTRO"), ID_1,
                DatosDePrueba.titular(), EscenarioReservas.APTO, e.estancia(), List.of(), LocalTime.of(15, 0), DatosDePrueba.AHORA));
        canal.desactivar();
        assertThrows(ReglaDominioException.class, () -> conciliar(ID_1, e.estancia()));
        assertTrue(e.reservas.buscarPorEstado(EstadoReserva.PENDIENTE).isEmpty());
    }

    @Test
    @DisplayName("RN-19: cancelar dos veces desde el canal es idempotente")
    void cancelacionIdempotente() {
        conciliar(ID_1, e.estancia());
        Optional<LiquidacionPolitica> primera = servicio.cancelarReservaExterna(CANAL, ID_1, DatosDePrueba.AHORA);
        Optional<LiquidacionPolitica> segunda = servicio.cancelarReservaExterna(CANAL, ID_1, DatosDePrueba.AHORA);
        assertTrue(primera.isPresent());
        assertTrue(segunda.isEmpty());
    }

    @Test
    @DisplayName("RN-19: cancelar un identificador desconocido se rechaza")
    void cancelarDesconocido() {
        assertThrows(ReglaDominioException.class, () -> servicio.cancelarReservaExterna(CANAL, ID_2, DatosDePrueba.AHORA));
    }

    // ---- RN-18 ----

    @Test
    @DisplayName("RN-18: una reserva externa que se solapa se rechaza y se registra como conflicto")
    void conflictoSeRegistra() {
        Reserva existente = e.crearReserva();
        ResultadoConciliacion r = conciliar(ID_1, DatosDePrueba.estancia(EscenarioReservas.ENTRADA.plusDays(1), 3));
        assertEquals(TipoResultado.CONFLICTO, r.tipo());
        assertNull(r.reserva());
        assertEquals(existente.getCodigo(), r.conflicto().getReservaVigente());
        assertEquals(1, conflictos.buscarSinRevisar().size());
        assertFalse(r.conflicto().estaRevisado());
    }

    @Test
    @DisplayName("RN-18: el conflicto nunca sobrescribe la reserva vigente")
    void noSobrescribeLaVigente() {
        Reserva existente = e.crearReserva();
        Estancia original = existente.getEstancia();
        conciliar(ID_1, DatosDePrueba.estancia(EscenarioReservas.ENTRADA.plusDays(1), 3));
        assertEquals(original, e.reservas.obtenerPorCodigo(existente.getCodigo()).orElseThrow().getEstancia());
        assertEquals(EstadoReserva.PENDIENTE, existente.getEstado());
        assertEquals(1, e.reservas.buscarActivasPorApartamento(EscenarioReservas.APTO).size());
        assertTrue(e.reservas.obtenerPorIdentificadorExterno(CanalOrigen.EXTERNO, ID_1).isEmpty());
    }

    @Test
    @DisplayName("RN-18: reenviar el mismo conflicto no genera un segundo registro")
    void conflictoNoSeDuplica() {
        e.crearReserva();
        Estancia solapada = DatosDePrueba.estancia(EscenarioReservas.ENTRADA, 2);
        conciliar(ID_1, solapada);
        ResultadoConciliacion repetido = conciliar(ID_1, solapada);
        assertEquals(TipoResultado.CONFLICTO, repetido.tipo());
        assertEquals(1, conflictos.buscarSinRevisar().size());
    }

    @Test
    @DisplayName("RN-18: sin solape la reserva externa sí se acepta")
    void sinSolapeSeAcepta() {
        e.crearReserva();
        ResultadoConciliacion r = conciliar(ID_1, DatosDePrueba.estancia(EscenarioReservas.ENTRADA.plusDays(10), 2));
        assertEquals(TipoResultado.ACEPTADA, r.tipo());
        assertTrue(conflictos.buscarSinRevisar().isEmpty());
    }

    @Test
    @DisplayName("RN-18: el conflicto se revisa una sola vez y con nota")
    void revisionDelConflicto() {
        e.crearReserva();
        ConflictoCanal conflicto = conciliar(ID_1, e.estancia()).conflicto();
        assertThrows(ReglaDominioException.class, () -> conflicto.marcarRevisado(" "));
        conflicto.marcarRevisado("Se contactó al canal");
        assertTrue(conflicto.estaRevisado());
        assertThrows(ReglaDominioException.class, () -> conflicto.marcarRevisado("otra vez"));
        assertTrue(conflictos.buscarSinRevisar().isEmpty());
    }

    @Test
    @DisplayName("RN-18: solo un canal externo puede generar un conflicto de canal")
    void soloCanalExterno() {
        assertThrows(ReglaDominioException.class, () -> new ConflictoCanal(new CodigoConflictoCanal("CON-1"),
                CanalOrigen.PORTAL, ID_1, EscenarioReservas.APTO, e.estancia(), new CodigoReserva("RES-1"), DatosDePrueba.AHORA));
    }
}
