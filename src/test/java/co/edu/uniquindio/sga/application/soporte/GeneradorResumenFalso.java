package co.edu.uniquindio.sga.application.soporte;

import co.edu.uniquindio.sga.domain.exception.GeneradorResumenNoDisponibleException;
import co.edu.uniquindio.sga.domain.port.GeneradorResumenPort;
import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;
import co.edu.uniquindio.sga.domain.valueobject.ResumenRedactado;

/** Puerto de IA hecho a mano para las pruebas (I-08): se programa para responder o para fallar, sin red. */
public class GeneradorResumenFalso implements GeneradorResumenPort {

    private final ResumenRedactado respuesta;
    private final RuntimeException fallo;
    private final boolean devuelveNulo;

    public int llamadas = 0;
    public DatosResumenDiario ultimosDatos;

    private GeneradorResumenFalso(ResumenRedactado respuesta, RuntimeException fallo, boolean devuelveNulo) {
        this.respuesta = respuesta;
        this.fallo = fallo;
        this.devuelveNulo = devuelveNulo;
    }

    /** Responde como lo haría un modelo real. */
    public static GeneradorResumenFalso redactando(String texto) {
        return new GeneradorResumenFalso(new ResumenRedactado(texto, true), null, false);
    }

    /** Responde como la implementación local simulada: hay texto, pero no hay modelo detrás. */
    public static GeneradorResumenFalso simulado(String texto) {
        return new GeneradorResumenFalso(new ResumenRedactado(texto, false), null, false);
    }

    /** La IA no está disponible por el motivo dado (apagada, tiempo agotado, error del proveedor). */
    public static GeneradorResumenFalso noDisponible(MotivoDegradacionIa motivo) {
        return new GeneradorResumenFalso(null,
                new GeneradorResumenNoDisponibleException(motivo, "Generador no disponible: " + motivo), false);
    }

    /** Falla con una excepción cualquiera, que el puerto no prometió. */
    public static GeneradorResumenFalso fallandoCon(RuntimeException fallo) {
        return new GeneradorResumenFalso(null, fallo, false);
    }

    /** Incumple el contrato y devuelve nulo. */
    public static GeneradorResumenFalso devolviendoNulo() {
        return new GeneradorResumenFalso(null, null, true);
    }

    @Override
    public ResumenRedactado generar(DatosResumenDiario datos) {
        llamadas++;
        ultimosDatos = datos;
        if (fallo != null) {
            throw fallo;
        }
        return devuelveNulo ? null : respuesta;
    }
}
