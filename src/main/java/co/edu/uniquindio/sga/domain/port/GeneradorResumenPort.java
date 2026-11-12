package co.edu.uniquindio.sga.domain.port;

import co.edu.uniquindio.sga.domain.exception.GeneradorResumenNoDisponibleException;
import co.edu.uniquindio.sga.domain.valueobject.DatosResumenDiario;
import co.edu.uniquindio.sga.domain.valueobject.ResumenRedactado;

/**
 * Puerto de salida del modelo de IA (10.2, I-01): redacta el resumen diario de llegadas y salidas. El dominio no
 * conoce al proveedor; hay una implementación local simulada (I-02) y una real.
 *
 * Contrato de las implementaciones:
 *
 *   Respetan un tiempo máximo de espera explícito (I-03) y, si se agota, lanzan
 *       {@link GeneradorResumenNoDisponibleException} con motivo {@code TIEMPO_AGOTADO}.
 *   Si están apagadas o el proveedor falla, lanzan la misma excepción con el motivo que corresponda.
 *   Solo reciben el {@link DatosResumenDiario}, sin datos personales de los huéspedes.
 *
 */

public interface GeneradorResumenPort {

    ResumenRedactado generar(DatosResumenDiario datos);
}
