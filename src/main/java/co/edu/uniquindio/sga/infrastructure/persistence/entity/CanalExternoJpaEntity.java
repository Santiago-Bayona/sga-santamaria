package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Canal de venta externo, su tipo, credencial y si está activo.
 */

@Entity
@Table(name = "canales_externos")
public class CanalExternoJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String codigo;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String credencial;

    @Column(nullable = false)
    private boolean activo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCredencial() {
        return credencial;
    }

    public void setCredencial(String credencial) {
        this.credencial = credencial;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
