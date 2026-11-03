package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Pago del folio. El monto es un Dinero embebido con su propia columna.
 */

@Embeddable
public class PagoEmbeddable {

    @Column(nullable = false)
    private int numero;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "monto", nullable = false, precision = 19, scale = 0))
    })
    private DineroEmbeddable monto;

    @Column(nullable = false, length = 40)
    private String medio;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "revierte_a")
    private Integer revierteA;

    public PagoEmbeddable() {
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public DineroEmbeddable getMonto() {
        return monto;
    }

    public void setMonto(DineroEmbeddable monto) {
        this.monto = monto;
    }

    public String getMedio() {
        return medio;
    }

    public void setMedio(String medio) {
        this.medio = medio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getRevierteA() {
        return revierteA;
    }

    public void setRevierteA(Integer revierteA) {
        this.revierteA = revierteA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PagoEmbeddable otro)) {
            return false;
        }
        return numero == otro.numero
                && Objects.equals(monto, otro.monto)
                && Objects.equals(medio, otro.medio)
                && Objects.equals(fecha, otro.fecha)
                && Objects.equals(revierteA, otro.revierteA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, monto, medio, fecha, revierteA);
    }
}
