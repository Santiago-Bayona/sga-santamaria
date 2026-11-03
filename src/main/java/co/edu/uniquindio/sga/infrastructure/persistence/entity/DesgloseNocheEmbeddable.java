package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Una noche cotizada: fecha, temporada, tarifa aplicada y subtotal.
 */

@Embeddable
public class DesgloseNocheEmbeddable {

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 64)
    private String temporada;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "tarifa_aplicada", nullable = false, precision = 19, scale = 0))
    })
    private DineroEmbeddable tarifaAplicada;

    @Column(name = "ocupantes_facturables", nullable = false)
    private int ocupantesFacturables;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "subtotal", nullable = false, precision = 19, scale = 0))
    })
    private DineroEmbeddable subtotal;

    public DesgloseNocheEmbeddable() {
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public DineroEmbeddable getTarifaAplicada() {
        return tarifaAplicada;
    }

    public void setTarifaAplicada(DineroEmbeddable tarifaAplicada) {
        this.tarifaAplicada = tarifaAplicada;
    }

    public int getOcupantesFacturables() {
        return ocupantesFacturables;
    }

    public void setOcupantesFacturables(int ocupantesFacturables) {
        this.ocupantesFacturables = ocupantesFacturables;
    }

    public DineroEmbeddable getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(DineroEmbeddable subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DesgloseNocheEmbeddable otro)) {
            return false;
        }
        return ocupantesFacturables == otro.ocupantesFacturables
                && Objects.equals(fecha, otro.fecha)
                && Objects.equals(temporada, otro.temporada)
                && Objects.equals(tarifaAplicada, otro.tarifaAplicada)
                && Objects.equals(subtotal, otro.subtotal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, temporada, tarifaAplicada, ocupantesFacturables, subtotal);
    }
}
