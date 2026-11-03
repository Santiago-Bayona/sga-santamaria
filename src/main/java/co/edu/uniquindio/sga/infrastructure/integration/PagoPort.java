package co.edu.uniquindio.sga.infrastructure.integration;

import java.math.BigDecimal;

/** Puerto de salida hacia una pasarela de pagos externa. El folio del dominio solo registra el resultado. */
/**
 * Puerto de la pasarela. El dominio pide cobrar; esta capa decide con qué proveedor.
 */
public interface PagoPort {

    ResultadoPagoExterno autorizar(BigDecimal monto, String medio, String referencia);

    record ResultadoPagoExterno(boolean aprobado, String referenciaExterna, String mensaje) {
    }
}
