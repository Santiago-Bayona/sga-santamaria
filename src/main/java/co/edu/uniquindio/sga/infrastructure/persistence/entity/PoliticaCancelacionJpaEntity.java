package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Política versionada. Los tramos viven en politica_tramos y la identidad es código más versión.
 */

@Entity
@Table(name = "politicas_cancelacion")
@IdClass(PoliticaCancelacionJpaEntity.PoliticaId.class)
public class PoliticaCancelacionJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String codigo;

    @Id
    @Column(nullable = false)
    private int version;

    @Column(nullable = false)
    private boolean vigente;

    @Column(name = "retencion_noshow", nullable = false)
    private int retencionNoShow;

    @Column(name = "creada_en", nullable = false)
    private LocalDateTime creadaEn;

    @ElementCollection
    @CollectionTable(name = "politica_tramos", joinColumns = {
            @JoinColumn(name = "codigo", referencedColumnName = "codigo"),
            @JoinColumn(name = "version", referencedColumnName = "version")
    })
    @OrderColumn(name = "indice")
    private List<TramoEmbeddable> tramos = new ArrayList<>();

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isVigente() {
        return vigente;
    }

    public void setVigente(boolean vigente) {
        this.vigente = vigente;
    }

    public int getRetencionNoShow() {
        return retencionNoShow;
    }

    public void setRetencionNoShow(int retencionNoShow) {
        this.retencionNoShow = retencionNoShow;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(LocalDateTime creadaEn) {
        this.creadaEn = creadaEn;
    }

    public List<TramoEmbeddable> getTramos() {
        return tramos;
    }

    public void setTramos(List<TramoEmbeddable> tramos) {
        this.tramos = tramos;
    }

    public static class PoliticaId implements Serializable {
        private String codigo;
        private int version;

        public PoliticaId() {
        }

        public PoliticaId(String codigo, int version) {
            this.codigo = codigo;
            this.version = version;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PoliticaId that)) return false;
            return version == that.version && Objects.equals(codigo, that.codigo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(codigo, version);
        }
    }
}
