package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Cargo del folio. Puede restar y puede apuntar al movimiento que revierte.
 */

@Embeddable
public class CargoEmbeddable {

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(nullable = false)
    private String concepto;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "monto", nullable = false, precision = 19, scale = 0))
    })
    private DineroEmbeddable monto;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private boolean resta;

    @Column(name = "revierte_a")
    private Integer revierteA;

    public CargoEmbeddable() {
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public DineroEmbeddable getMonto() {
        return monto;
    }

    public void setMonto(DineroEmbeddable monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isResta() {
        return resta;
    }

    public void setResta(boolean resta) {
        this.resta = resta;
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
        if (!(o instanceof CargoEmbeddable otro)) {
            return false;
        }
        return numero == otro.numero && resta == otro.resta
                && Objects.equals(tipo, otro.tipo)
                && Objects.equals(concepto, otro.concepto)
                && Objects.equals(monto, otro.monto)
                && Objects.equals(fecha, otro.fecha)
                && Objects.equals(revierteA, otro.revierteA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, tipo, concepto, monto, fecha, resta, revierteA);
    }
}
