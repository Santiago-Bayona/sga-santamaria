package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/** Periodo anual de una temporada. Vive en su propia colección para no anidar @ElementCollection. */
@Embeddable
public class PeriodoTemporadaEmbeddable {

    @Column(name = "id_temporada", nullable = false, length = 64)
    private String idTemporada;

    @Column(name = "mes_inicio", nullable = false)
    private int mesInicio;

    @Column(name = "dia_inicio", nullable = false)
    private int diaInicio;

    @Column(name = "mes_fin", nullable = false)
    private int mesFin;

    @Column(name = "dia_fin", nullable = false)
    private int diaFin;

    public PeriodoTemporadaEmbeddable() {
    }

    public String getIdTemporada() {
        return idTemporada;
    }

    public void setIdTemporada(String idTemporada) {
        this.idTemporada = idTemporada;
    }

    public int getMesInicio() {
        return mesInicio;
    }

    public void setMesInicio(int mesInicio) {
        this.mesInicio = mesInicio;
    }

    public int getDiaInicio() {
        return diaInicio;
    }

    public void setDiaInicio(int diaInicio) {
        this.diaInicio = diaInicio;
    }

    public int getMesFin() {
        return mesFin;
    }

    public void setMesFin(int mesFin) {
        this.mesFin = mesFin;
    }

    public int getDiaFin() {
        return diaFin;
    }

    public void setDiaFin(int diaFin) {
        this.diaFin = diaFin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PeriodoTemporadaEmbeddable otro)) {
            return false;
        }
        return mesInicio == otro.mesInicio && diaInicio == otro.diaInicio && mesFin == otro.mesFin
                && diaFin == otro.diaFin && Objects.equals(idTemporada, otro.idTemporada);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTemporada, mesInicio, diaInicio, mesFin, diaFin);
    }
}
