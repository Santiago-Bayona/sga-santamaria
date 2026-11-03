package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Temporada del alojamiento. El flag base distingue la temporada permanente de las especiales.
 */

@Embeddable
public class TemporadaEmbeddable {

    @Column(name = "id_temporada", nullable = false, length = 64)
    private String idTemporada;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private boolean base;

    public TemporadaEmbeddable() {
    }

    public String getIdTemporada() {
        return idTemporada;
    }

    public void setIdTemporada(String idTemporada) {
        this.idTemporada = idTemporada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemporadaEmbeddable otro)) {
            return false;
        }
        return base == otro.base && Objects.equals(idTemporada, otro.idTemporada) && Objects.equals(nombre, otro.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTemporada, nombre, base);
    }
}
