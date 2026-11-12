package co.edu.uniquindio.sga.infrastructure.ia;

import co.edu.uniquindio.sga.domain.port.GeneradorResumenPort;
import co.edu.uniquindio.sga.domain.service.ResumenBasicoDiario;
import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.ResumenRedactado;

/**
 * Implementación local alternativa (I-02): redacta con una plantilla fija, sin red y sin credenciales. Permite
 * ejecutar y probar todo el sistema sin conexión. Se marca como no generada por modelo.
 */
public class ResumenSimuladoAdapter implements GeneradorResumenPort {

    @Override
    public ResumenRedactado generar(DatosResumenDiario datos) {
        return new ResumenRedactado(ResumenBasicoDiario.redactar(datos), false);
    }
}
