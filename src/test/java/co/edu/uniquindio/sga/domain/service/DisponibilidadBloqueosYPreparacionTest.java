package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Bloqueo;
import co.edu.uniquindio.sga.domain.soporte.*;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.*;
import static org.junit.jupiter.api.Assertions.*;

/** RN-07 (bloqueo vigente), RN-20 (tiempo de preparación en la rotación del mismo día) y apartamento inactivo. */
class DisponibilidadBloqueosYPreparacionTest {

    private static final LocalDate ENTRADA = LocalDate.of(2026, 9, 10);

    private Apartamento apartamento;
    private ReservaRepositoryEnMemoria reservas;
    private BloqueoRepositoryEnMemoria bloqueos;

    @BeforeEach
    void preparar() {
        apartamento = DatosDePrueba.apartamento("APT-301", 4);
        reservas = new ReservaRepositoryEnMemoria();
        bloqueos = new BloqueoRepositoryEnMemoria();
    }

    private DisponibilidadApartamentoService servicio(Duration preparacion) {
        return new DisponibilidadApartamentoService(reservas, bloqueos,
                new ApartamentoRepositoryEnMemoria().con(apartamento),
                new AlojamientoRepositoryEnMemoria(DatosDePrueba.alojamiento(configuracion(preparacion))));
    }

    private Bloqueo bloqueo(String codigo, Estancia rango) {
        Bloqueo bloqueo = new Bloqueo(new CodigoBloqueo(codigo), apartamento.getIdentificacion(), rango,
                new MotivoBloqueo("Mantenimiento"), AHORA);
        bloqueos.guardar(bloqueo);
        return bloqueo;
    }

    //RN-07

    @Test
    @DisplayName("RN-07: un bloqueo vigente sobre alguna noche deja el apartamento no disponible")
    void noPermitirReservarSobreUnBloqueoVigente() {
        bloqueo("BLQ-1", estancia(ENTRADA, 3));

        Disponibilidad resultado = servicio(Duration.ofHours(2)).evaluar(apartamento, estancia(ENTRADA.plusDays(2), 3), 2, null);

        assertFalse(resultado.estaDisponible());
        assertTrue(resultado.motivos().contains(MotivoNoDisponibilidad.SOLAPA_BLOQUEO));
    }

    @Test
    @DisplayName("RN-07: un bloqueo levantado libera las noches")
    void permitirReservarCuandoElBloqueoFueLevantado() {
        Bloqueo bloqueo = bloqueo("BLQ-1", estancia(ENTRADA, 3));
        bloqueo.levantar();
        bloqueos.guardar(bloqueo);

        assertTrue(servicio(Duration.ofHours(2)).estaDisponible(apartamento, estancia(ENTRADA, 3), 2));
    }

    @Test
    @DisplayName("RN-07: un bloqueo en otras fechas no afecta")
    void permitirReservarFueraDelRangoBloqueado() {
        bloqueo("BLQ-1", estancia(ENTRADA, 3));

        assertTrue(servicio(Duration.ofHours(2)).estaDisponible(apartamento, estancia(ENTRADA.plusDays(3), 2), 2));
    }

    // RN-20

    @Test
    @DisplayName("RN-20: si la preparación no cabe entre la salida y la entrada, no hay rotación el mismo día")
    void noPermitirRotacionMismoDiaSiLaPreparacionNoCabe() {
        reservas.con(reservaConfirmada("RES-1", apartamento, estancia(ENTRADA, 3)));        // sale el 13 a las 11:00

        // Preparación de 5 h > 4 h disponibles (11:00 → 15:00)
        Disponibilidad siguiente = servicio(Duration.ofHours(5)).evaluar(apartamento, estancia(ENTRADA.plusDays(3), 2), 2, null);
        Disponibilidad anterior = servicio(Duration.ofHours(5)).evaluar(apartamento, estancia(ENTRADA.minusDays(2), 2), 2, null);

        assertTrue(siguiente.motivos().contains(MotivoNoDisponibilidad.TIEMPO_PREPARACION_INSUFICIENTE));
        assertTrue(anterior.motivos().contains(MotivoNoDisponibilidad.TIEMPO_PREPARACION_INSUFICIENTE));
    }

    @Test
    @DisplayName("RN-20: si la preparación cabe, la rotación el mismo día es posible")
    void permitirRotacionMismoDiaSiLaPreparacionCabe() {
        reservas.con(reservaConfirmada("RES-1", apartamento, estancia(ENTRADA, 3)));

        assertTrue(servicio(Duration.ofHours(4)).estaDisponible(apartamento, estancia(ENTRADA.plusDays(3), 2), 2));
    }

    @Test
    @DisplayName("RN-20: con un día libre entre estancias no se exige nada de la preparación")
    void permitirSiHayUnDiaLibreEntreEstancias() {
        reservas.con(reservaConfirmada("RES-1", apartamento, estancia(ENTRADA, 3)));

        assertTrue(servicio(Duration.ofHours(5)).estaDisponible(apartamento, estancia(ENTRADA.plusDays(4), 2), 2));
    }

    //  Apartamento inactivo

    @Test
    @DisplayName("Un apartamento no activo no está disponible, y buscarDisponibles solo devuelve activos libres")
    void noOfrecerApartamentoInactivo() {
        Apartamento inactivo = apartamentoSinTarifas("APT-INACTIVO", 4);
        DisponibilidadApartamentoService servicio = new DisponibilidadApartamentoService(reservas, bloqueos,
                new ApartamentoRepositoryEnMemoria().con(apartamento, inactivo),
                new AlojamientoRepositoryEnMemoria(DatosDePrueba.alojamiento()));

        Disponibilidad resultado = servicio.evaluar(inactivo, estancia(ENTRADA, 2), 2, null);
        List<Apartamento> disponibles = servicio.buscarDisponibles(estancia(ENTRADA, 2), 2);

        assertTrue(resultado.motivos().contains(MotivoNoDisponibilidad.APARTAMENTO_INACTIVO));
        assertEquals(1, disponibles.size());
        assertEquals(apartamento.getIdentificacion(), disponibles.get(0).getIdentificacion());
    }
}
