package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static co.edu.uniquindio.sga.domain.soporte.DatosDePrueba.*;
import static org.junit.jupiter.api.Assertions.*;

/** 7.3/7.4 (activación y tarifas), RN-11 y F-09 (estado operativo), F-03/RN-22 (capacidad), galería 1–10. */
class ApartamentoTest {

    private static final List<IdTemporada> TODAS = List.of(TEMPORADA_BASE, TEMPORADA_MEDIA, TEMPORADA_ALTA);

    private Apartamento nuevo;

    @BeforeEach
    void preparar() {
        nuevo = apartamentoSinTarifas("APT-301", 4);
    }

    // 7.4 activación y tarifas

    @Test
    @DisplayName("7.4: un apartamento nace inactivo y no se activa sin tarifa en todas las temporadas")
    void noActivarSinTarifaEnTodasLasTemporadas() {
        assertFalse(nuevo.estaActivo());
        nuevo.definirTarifa(new Tarifa(TEMPORADA_BASE, TARIFA_BASE), AHORA);

        assertThrows(ReglaDominioException.class, () -> nuevo.activar(TODAS));
        assertFalse(nuevo.estaActivo());
        assertThrows(ReglaDominioException.class, () -> nuevo.verificarQueAceptaReservas());
    }

    @Test
    @DisplayName("7.4: con tarifa en todas las temporadas se activa y acepta reservas")
    void activarConTarifasCompletas() {
        Apartamento completo = apartamento("APT-302", 4);

        assertTrue(completo.estaActivo());
        completo.verificarQueAceptaReservas();
        assertTrue(completo.tieneTarifasCompletas(TODAS));
    }

    @Test
    @DisplayName("7.4: redefinir la tarifa de una temporada reemplaza la vigente y deja histórico")
    void redefinirTarifaConservaElHistorico() {
        Apartamento apto = apartamento("APT-302", 4);

        apto.definirTarifa(new Tarifa(TEMPORADA_BASE, Dinero.pesos(120_000)), AHORA.plusDays(1));

        assertEquals(Dinero.pesos(120_000), apto.tarifaEn(TEMPORADA_BASE).orElseThrow().valorPorOcupante());
        assertEquals(3, apto.getTarifas().size());
        assertEquals(4, apto.getHistorialTarifas().size());
        assertThrows(ReglaDominioException.class, () -> new Tarifa(TEMPORADA_BASE, Dinero.CERO));
    }

    // 7.3

    @Test
    @DisplayName("7.3: no se retira de la venta con reservas activas o futuras")
    void noRetirarDeVentaConReservasActivasOFuturas() {
        Apartamento apto = apartamento("APT-302", 4);

        assertThrows(ReglaDominioException.class, () -> apto.retirarDeVenta(true));
        assertTrue(apto.estaActivo());
        apto.retirarDeVenta(false);
        assertFalse(apto.estaActivo());
    }

    //  F-03 / RN-22 capacidad

    @Test
    @DisplayName("F-03: cambiar la capacidad indica si disminuyó (para advertir al administrador)")
    void informarSiLaCapacidadDisminuye() {
        assertTrue(nuevo.cambiarCapacidad(new Capacidad(2)));
        assertFalse(nuevo.cambiarCapacidad(new Capacidad(6)));
        assertEquals(6, nuevo.getCapacidad().valor());
        assertThrows(ReglaDominioException.class, () -> nuevo.cambiarCapacidad(null));
    }

    // Galería

    @Test
    @DisplayName("7.3: la galería exige entre 1 y 10 imágenes y una principal válida")
    void validarGaleria() {
        assertThrows(ReglaDominioException.class, () -> new GaleriaImagenes(List.of(), 0));
        assertThrows(ReglaDominioException.class, () -> new GaleriaImagenes(
                java.util.Collections.nCopies(11, "https://i.test/x.jpg"), 0));
        assertThrows(ReglaDominioException.class, () -> new GaleriaImagenes(List.of("https://i.test/1.jpg"), 1));
        assertEquals("https://i.test/2.jpg",
                new GaleriaImagenes(List.of("https://i.test/1.jpg", "https://i.test/2.jpg"), 1).principal());
    }

    //  RN-11 / F-09 estado operativo

    @Test
    @DisplayName("RN-11: solo un apartamento activo y PREPARADO puede recibir un grupo")
    void recibirGrupoSoloSiEstaActivoYPreparado() {
        assertFalse(nuevo.puedeRecibirGrupo());                         // está PREPARADO pero inactivo
        Apartamento apto = apartamento("APT-302", 4);
        assertTrue(apto.puedeRecibirGrupo());
        apto.declararFueraDeServicio();
        assertFalse(apto.puedeRecibirGrupo());
    }

    @Test
    @DisplayName("F-09: ciclo operativo PREPARADO → OCUPADO → PENDIENTE_PREPARACION → EN_PREPARACION → PREPARADO")
    void recorrerElCicloOperativo() {
        Apartamento apto = apartamento("APT-302", 4);

        apto.marcarOcupado();
        assertEquals(EstadoOperativo.OCUPADO, apto.getEstadoOperativo());
        apto.marcarPendientePreparacion();
        apto.marcarEnPreparacion();
        apto.marcarPreparado();
        assertEquals(EstadoOperativo.PREPARADO, apto.getEstadoOperativo());
    }

    @Test
    @DisplayName("F-09: no se saltan estados operativos")
    void noSaltarEstadosOperativos() {
        Apartamento apto = apartamento("APT-302", 4);

        assertThrows(ReglaDominioException.class, apto::marcarPendientePreparacion);   // no está OCUPADO
        assertThrows(ReglaDominioException.class, apto::marcarEnPreparacion);
        assertThrows(ReglaDominioException.class, apto::marcarPreparado);              // ya está PREPARADO
        apto.marcarOcupado();
        assertThrows(ReglaDominioException.class, apto::marcarOcupado);                // ya OCUPADO
    }

    @Test
    @DisplayName("7.6: no se declara fuera de servicio un apartamento OCUPADO ni uno que ya lo está")
    void restringirFueraDeServicio() {
        Apartamento apto = apartamento("APT-302", 4);
        apto.marcarOcupado();
        assertThrows(ReglaDominioException.class, apto::declararFueraDeServicio);

        Apartamento otro = apartamento("APT-303", 4);
        otro.declararFueraDeServicio();
        assertThrows(ReglaDominioException.class, otro::declararFueraDeServicio);
        otro.marcarPreparado();
        assertEquals(EstadoOperativo.PREPARADO, otro.getEstadoOperativo());
    }
}
