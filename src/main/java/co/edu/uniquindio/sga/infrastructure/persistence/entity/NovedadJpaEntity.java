package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Novedad de un apartamento: descripcion, gravedad y autor.
 */

@Entity
@Table(name = "novedades")
public class NovedadJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String codigo;

    @Column(name = "identificacion_apartamento", nullable = false, length = 64)
    private String identificacionApartamento;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String gravedad;

    @Column(nullable = false)
    private boolean resuelta;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getGravedad() {
        return gravedad;
    }

    public void setGravedad(String gravedad) {
        this.gravedad = gravedad;
    }

    public boolean isResuelta() {
        return resuelta;
    }

    public void setResuelta(boolean resuelta) {
        this.resuelta = resuelta;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }
}
