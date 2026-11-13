package co.edu.uniquindio.sga.infrastructure.ia;

import co.edu.uniquindio.sga.domain.exception.GeneradorResumenNoDisponibleException;
import co.edu.uniquindio.sga.domain.port.GeneradorResumenPort;
import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;
import co.edu.uniquindio.sga.domain.valueobject.ResumenRedactado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 10.2: apagar la IA o cambiar de proveedor es solo configuración. Aquí se prueba qué adaptador se elige. */
class IaConfigTest {

    private static final DatosResumenDiario DATOS = new DatosResumenDiario(LocalDate.of(2026, 11, 12), List.of(), List.of());

    private static IaProperties config(boolean habilitada, String proveedor) {
        return new IaProperties(habilitada, proveedor, "http://localhost:1/v1", "m", 1000, "k");
    }

    @Test
    @DisplayName("10.2: con la IA apagada se elige el adaptador deshabilitado, sin importar el proveedor")
    void apagada() {
        GeneradorResumenPort generador = IaConfig.crear(config(false, IaProperties.PROVEEDOR_OPENAI_COMPATIBLE));

        GeneradorResumenNoDisponibleException e = assertThrows(GeneradorResumenNoDisponibleException.class,
                () -> generador.generar(DATOS));
        assertEquals(MotivoDegradacionIa.IA_DESHABILITADA, e.getMotivo());
    }

    @Test
    @DisplayName("I-02: encendida con proveedor simulado responde sin red y se marca como no generada por modelo")
    void simulada() {
        ResumenRedactado resumen = IaConfig.crear(config(true, IaProperties.PROVEEDOR_SIMULADO)).generar(DATOS);

        assertFalse(resumen.generadoPorModelo());
        assertTrue(resumen.texto().contains("0 llegadas y 0 salidas previstas"));
    }

    @Test
    @DisplayName("Encendida con openai-compatible se elige el adaptador real")
    void real() {
        assertTrue(IaConfig.crear(config(true, IaProperties.PROVEEDOR_OPENAI_COMPATIBLE)) instanceof ResumenLlmAdapter);
    }

    @Test
    @DisplayName("I-05: un proveedor mal escrito no tumba el arranque: el resumen sale sin IA")
    void proveedorDesconocido() {
        GeneradorResumenPort generador = IaConfig.crear(config(true, "gemin"));

        GeneradorResumenNoDisponibleException e = assertThrows(GeneradorResumenNoDisponibleException.class,
                () -> generador.generar(DATOS));
        assertEquals(MotivoDegradacionIa.ERROR_PROVEEDOR, e.getMotivo());
    }

    @Test
    @DisplayName("Valores por defecto: proveedor simulado y tiempo de espera de 5 segundos")
    void valoresPorDefecto() {
        IaProperties vacia = new IaProperties(false, null, null, null, 0, null);

        assertEquals(IaProperties.PROVEEDOR_SIMULADO, vacia.proveedor());
        assertEquals(5000, vacia.tiempoEsperaMs());
    }
}
