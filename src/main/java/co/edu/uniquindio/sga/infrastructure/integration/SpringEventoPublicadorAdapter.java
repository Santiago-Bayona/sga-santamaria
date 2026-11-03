package co.edu.uniquindio.sga.infrastructure.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publica eventos de aplicacion en el bus de Spring. El dominio no depende de ApplicationEventPublisher.
 */

@Component
public class SpringEventoPublicadorAdapter implements EventoPublicadorPort {

    private static final Logger LOG = LoggerFactory.getLogger(SpringEventoPublicadorAdapter.class);

    private final ApplicationEventPublisher publisher;

    public SpringEventoPublicadorAdapter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publicar(String tipo, Object payload) {
        LOG.debug("Evento técnico {} publicado en el bus interno (reemplazable por Kafka/Rabbit).", tipo);
        publisher.publishEvent(new EventoTecnico(tipo, payload));
    }

    public record EventoTecnico(String tipo, Object payload) {
    }
}
