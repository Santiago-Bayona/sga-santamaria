package co.edu.uniquindio.sga.infrastructure.config;

import org.springframework.stereotype.Component;

import java.util.UUID;

/** Generación técnica de UUIDs (códigos de negocio de reserva siguen en GeneradorCodigoReserva). */
@Component
public class IdentificadorTecnico {

    public String uuid() {
        return UUID.randomUUID().toString();
    }
}
