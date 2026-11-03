package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Ultimo consecutivo emitido para los códigos de reserva de un año.
 */

@Entity
@Table(name = "secuencias_codigo")
public class SecuenciaCodigoJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String nombre;

    @Column(nullable = false)
    private long valor;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }
}
