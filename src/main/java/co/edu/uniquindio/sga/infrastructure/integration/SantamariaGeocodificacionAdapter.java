package co.edu.uniquindio.sga.infrastructure.integration;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Stub de mapas: Montenegro / Santamaría. Sustituible por un cliente HTTP de geocodificación. */
@Component
public class SantamariaGeocodificacionAdapter implements GeocodificacionPort {

    @Override
    public Coordenadas geocodificar(String direccion) {
        return new Coordenadas(new BigDecimal("4.5650000"), new BigDecimal("-75.7510000"));
    }
}
