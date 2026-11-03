package co.edu.uniquindio.sga.infrastructure.persistence.adapter;

import co.edu.uniquindio.sga.domain.repository.GeneradorCodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.infrastructure.persistence.entity.SecuenciaCodigoJpaEntity;
import co.edu.uniquindio.sga.infrastructure.persistence.jpa.SecuenciaCodigoJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Year;

/**
 * Genera códigos RES-AAAA-00001 con una secuencia persistida en H2.
 */
@Component
public class GeneradorCodigoReservaAdapter implements GeneradorCodigoReserva {

    private static final String SECUENCIA = "reserva";

    private final SecuenciaCodigoJpaRepository secuencias;
    private final Clock clock;

    public GeneradorCodigoReservaAdapter(SecuenciaCodigoJpaRepository secuencias, Clock clock) {
        this.secuencias = secuencias;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CodigoReserva generar() {
        int anio = Year.now(clock).getValue();
        String clave = SECUENCIA + "-" + anio;
        SecuenciaCodigoJpaEntity secuencia = secuencias.findById(clave).orElseGet(() -> {
            SecuenciaCodigoJpaEntity nueva = new SecuenciaCodigoJpaEntity();
            nueva.setNombre(clave);
            nueva.setValor(0);
            return nueva;
        });
        long siguiente = secuencia.getValor() + 1;
        secuencia.setValor(siguiente);
        secuencias.save(secuencia);
        return new CodigoReserva("RES-%d-%05d".formatted(anio, siguiente));
    }
}
