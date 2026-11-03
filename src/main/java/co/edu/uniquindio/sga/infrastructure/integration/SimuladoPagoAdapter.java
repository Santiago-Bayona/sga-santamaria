package co.edu.uniquindio.sga.infrastructure.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/** Pasarela simulada: aprueba todo monto > 0. Se reemplaza por un cliente HTTP real. */
@Component
public class SimuladoPagoAdapter implements PagoPort {

    private static final Logger LOG = LoggerFactory.getLogger(SimuladoPagoAdapter.class);

    @Override
    public ResultadoPagoExterno autorizar(BigDecimal monto, String medio, String referencia) {
        boolean aprobado = monto != null && monto.signum() > 0;
        String ext = "PAY-" + UUID.randomUUID();
        LOG.info("Pasarela simulada {} {} {} → {} ({})", medio, monto, referencia, ext, aprobado);
        return new ResultadoPagoExterno(aprobado, ext, aprobado ? "Autorizado" : "Monto inválido");
    }
}
