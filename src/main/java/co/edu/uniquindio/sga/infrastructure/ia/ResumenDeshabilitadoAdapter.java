package co.edu.uniquindio.sga.infrastructure.ia;

import co.edu.uniquindio.sga.domain.exception.GeneradorResumenNoDisponibleException;
import co.edu.uniquindio.sga.domain.port.GeneradorResumenPort;
import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.MotivoDegradacionIa;
import co.edu.uniquindio.sga.domain.valueobject.ResumenRedactado;

/** IA apagada por configuración (10.2): el resumen sale sin IA y el sistema sigue operando con normalidad. */
public class ResumenDeshabilitadoAdapter implements GeneradorResumenPort {

    @Override
    public ResumenRedactado generar(DatosResumenDiario datos) {
        throw new GeneradorResumenNoDisponibleException(MotivoDegradacionIa.IA_DESHABILITADA,
                "La IA está deshabilitada (sga.ia.habilitada=false).");
    }
}
