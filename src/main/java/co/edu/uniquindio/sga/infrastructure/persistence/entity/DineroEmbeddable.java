package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Objects;

/** Pesos colombianos persistidos. El nombre de columna se redefine en cada uso. */
@Embeddable
public class DineroEmbeddable {

    @Column(name = "valor", nullable = false, precision = 19, scale = 0)
    private BigDecimal valor;

    public DineroEmbeddable() {
    }

    public DineroEmbeddable(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DineroEmbeddable otro)) {
            return false;
        }
        return Objects.equals(valor, otro.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}
