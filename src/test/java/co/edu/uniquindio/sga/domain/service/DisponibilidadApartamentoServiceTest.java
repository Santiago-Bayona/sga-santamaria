package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.soporte.*;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Disponibilidad;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.MotivoCancelacion;
import co.edu.uniquindio.sga.domain.valueobject.MotivoNoDisponibilidad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.estancia;
import static org.junit.jupiter.api.Assertions.*;

/** RN-01 (no solapar reservas activas) y RN-02 (capacidad como tope rígido) en DisponibilidadApartamentoService. */
class DisponibilidadApartamentoServiceTest {

    private static final LocalDate ENTRADA = LocalDate.of(2026, 9, 10);

    private Apartamento apartamento;
    private ReservaRepositoryEnMemoria reservas;
    private DisponibilidadApartamentoService servicio;

    @BeforeEach
    void preparar() {
        apartamento = DatosDePrueba.apartamento("APT-301", 4);
        reservas = new ReservaRepositoryEnMemoria();
        servicio = new DisponibilidadApartamentoService(reservas, new BloqueoRepositoryEnMemoria(),
                new ApartamentoRepositoryEnMemoria().con(apartamento),
                new AlojamientoRepositoryEnMemoria(DatosDePrueba.alojamiento()));
    }

    private Reserva reservaExistente(String codigo, Estancia estancia) {
        Reserva reserva = DatosDePrueba.reservaConfirmada(codigo, apartamento, estancia);
        reservas.con(reserva);
        return reserva;
    }

    // RN-01

    @Test
    @DisplayName("RN-01: una estancia que se cruza con una reserva activa deja el apartamento no disponible")
    void noPermitirSolapamientoReservaActiva() {
        reservaExistente("RES-1", estancia(ENTRADA, 3));                       // 10 → 13

        Disponibilidad resultado = servicio.evaluar(apartamento, estancia(ENTRADA.plusDays(2), 3), 2, null); // 12 → 15

        assertFalse(resultado.estaDisponible());
        assertTrue(resultado.motivos().contains(MotivoNoDisponibilidad.SOLAPA_RESERVA_ACTIVA));
        assertThrows(ReglaDominioException.class,
                () -> servicio.verificarDisponible(apartamento, estancia(ENTRADA.plusDays(2), 3), 2, null));
    }

    @Test
    @DisplayName("RN-01: estancias contiguas (salida = entrada) no se solapan")
    void permitirEstanciaQueEmpiezaElDiaDeSalidaDeOtra() {
        reservaExistente("RES-1", estancia(ENTRADA, 3));                       // 10 → 13

        Disponibilidad resultado = servicio.evaluar(apartamento, estancia(ENTRADA.plusDays(3), 2), 2, null); // 13 → 15

        assertTrue(resultado.estaDisponible());
    }

    @Test
    @DisplayName("RN-01: una reserva cancelada deja de ser activa y libera sus noches")
    void permitirReservarSobreNochesDeUnaReservaCancelada() {
        Reserva existente = reservaExistente("RES-1", estancia(ENTRADA, 3));
        existente.cancelar(new MotivoCancelacion("Cambio de planes"));

        assertTrue(servicio.estaDisponible(apartamento, estancia(ENTRADA, 3), 2));
    }

    @Test
    @DisplayName("RN-01/RN-14: al modificar una reserva se excluye a sí misma del cruce")
    void permitirModificarUnaReservaSobreSusPropiasNoches() {
        Reserva existente = reservaExistente("RES-1", estancia(ENTRADA, 3));

        assertTrue(servicio.evaluar(apartamento, estancia(ENTRADA, 4), 2, existente.getCodigo()).estaDisponible());
        assertFalse(servicio.evaluar(apartamento, estancia(ENTRADA, 4), 2, new CodigoReserva("OTRA")).estaDisponible());
    }

    // RN-02

    @Test
    @DisplayName("RN-02: un grupo igual a la capacidad cabe")
    void admitirOcupantesIgualesALaCapacidad() {
        assertTrue(servicio.estaDisponible(apartamento, estancia(ENTRADA, 2), 4));
    }

    @Test
    @DisplayName("RN-02: un grupo que supera la capacidad deja el apartamento no disponible")
    void noAdmitirOcupantesQueSuperanLaCapacidad() {
        Disponibilidad resultado = servicio.evaluar(apartamento, estancia(ENTRADA, 2), 5, null);

        assertFalse(resultado.estaDisponible());
        assertTrue(resultado.motivos().contains(MotivoNoDisponibilidad.CAPACIDAD_INSUFICIENTE));
        assertThrows(ReglaDominioException.class,
                () -> servicio.verificarDisponible(apartamento, estancia(ENTRADA, 2), 5, null));
    }

    @Test
    @DisplayName("RN-01 + RN-02: se informan todos los motivos a la vez, no solo el primero")
    void informarTodosLosMotivosDeNoDisponibilidad() {
        reservaExistente("RES-1", estancia(ENTRADA, 3));

        Disponibilidad resultado = servicio.evaluar(apartamento, estancia(ENTRADA, 3), 6, null);

        assertTrue(resultado.motivos().contains(MotivoNoDisponibilidad.SOLAPA_RESERVA_ACTIVA));
        assertTrue(resultado.motivos().contains(MotivoNoDisponibilidad.CAPACIDAD_INSUFICIENTE));
    }
}
