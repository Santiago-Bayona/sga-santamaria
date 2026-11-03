package co.edu.uniquindio.sga.infrastructure.integration;

/** Publicación de eventos técnicos. En prod se reemplaza por Kafka/RabbitMQ. */
/**
 * Puerto para publicar hechos ya ocurridos sin acoplar el dominio a Spring.
 */
public interface EventoPublicadorPort {

    void publicar(String tipo, Object payload);
}
