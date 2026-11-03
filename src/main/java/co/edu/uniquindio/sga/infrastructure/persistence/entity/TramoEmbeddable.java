package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Tramo de la política de cancelación: días de antelación y porcentaje retenido.
 */

@Embeddable
public class TramoEmbeddable {

    @Column(name = "dias_antelacion_minima", nullable = false)
    private int diasAntelacionMinima;

    @Column(nullable = false)
    private int retencion;

    public TramoEmbeddable() {
    }

    public TramoEmbeddable(int diasAntelacionMinima, int retencion) {
        this.diasAntelacionMinima = diasAntelacionMinima;
        this.retencion = retencion;
    }

    public int getDiasAntelacionMinima() {
        return diasAntelacionMinima;
    }

    public void setDiasAntelacionMinima(int diasAntelacionMinima) {
        this.diasAntelacionMinima = diasAntelacionMinima;
    }

    public int getRetencion() {
        return retencion;
    }

    public void setRetencion(int retencion) {
        this.retencion = retencion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TramoEmbeddable otro)) {
            return false;
        }
        return diasAntelacionMinima == otro.diasAntelacionMinima && retencion == otro.retencion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(diasAntelacionMinima, retencion);
    }
}
