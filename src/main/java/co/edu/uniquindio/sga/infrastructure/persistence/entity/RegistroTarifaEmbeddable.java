package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Una entrada del historial de tarifas del apartamento. La vigente es la ultima aplicada por temporada.
 */

@Embeddable
public class RegistroTarifaEmbeddable {

    @Column(nullable = false, length = 64)
    private String temporada;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "valor_por_ocupante", nullable = false, precision = 19, scale = 0))
    })
    private DineroEmbeddable valorPorOcupante;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDateTime vigenteDesde;

    public RegistroTarifaEmbeddable() {
    }

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public DineroEmbeddable getValorPorOcupante() {
        return valorPorOcupante;
    }

    public void setValorPorOcupante(DineroEmbeddable valorPorOcupante) {
        this.valorPorOcupante = valorPorOcupante;
    }

    public LocalDateTime getVigenteDesde() {
        return vigenteDesde;
    }

    public void setVigenteDesde(LocalDateTime vigenteDesde) {
        this.vigenteDesde = vigenteDesde;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegistroTarifaEmbeddable otro)) {
            return false;
        }
        return Objects.equals(temporada, otro.temporada)
                && Objects.equals(valorPorOcupante, otro.valorPorOcupante)
                && Objects.equals(vigenteDesde, otro.vigenteDesde);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temporada, valorPorOcupante, vigenteDesde);
    }
}
