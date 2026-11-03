package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Tabla bloqueos. El rango reutiliza EstanciaEmbeddable con columnas fecha_inicio y fecha_fin.
 */

@Entity
@Table(name = "bloqueos")
public class BloqueoJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String codigo;

    @Column(name = "identificacion_apartamento", nullable = false, length = 64)
    private String identificacionApartamento;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fechaEntrada", column = @Column(name = "fecha_inicio", nullable = false)),
            @AttributeOverride(name = "fechaSalida", column = @Column(name = "fecha_fin", nullable = false))
    })
    private EstanciaEmbeddable rango;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(nullable = false)
    private boolean vigente;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getIdentificacionApartamento() {
        return identificacionApartamento;
    }

    public void setIdentificacionApartamento(String identificacionApartamento) {
        this.identificacionApartamento = identificacionApartamento;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public EstanciaEmbeddable getRango() {
        return rango;
    }

    public void setRango(EstanciaEmbeddable rango) {
        this.rango = rango;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public boolean isVigente() {
        return vigente;
    }

    public void setVigente(boolean vigente) {
        this.vigente = vigente;
    }
}
