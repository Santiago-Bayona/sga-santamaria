package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Conflicto detectado al conciliar un canal. La estancia usa las columnas fecha_entrada y fecha_salida.
 */

@Entity
@Table(name = "conflictos_canal")
public class ConflictoCanalJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String codigo;

    @Column(nullable = false, length = 20)
    private String canal;

    @Column(name = "identificador_externo", nullable = false, length = 120)
    private String identificadorExterno;

    @Column(name = "identificacion_apartamento", nullable = false, length = 64)
    private String identificacionApartamento;

    @Embedded
    private EstanciaEmbeddable estancia;

    @Column(name = "codigo_reserva_vigente", nullable = false, length = 64)
    private String codigoReservaVigente;

    @Column(name = "fecha_deteccion", nullable = false)
    private LocalDateTime fechaDeteccion;

    @Column(nullable = false)
    private boolean revisado;

    @Column(name = "nota_revision", length = 1000)
    private String notaRevision;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getIdentificadorExterno() {
        return identificadorExterno;
    }

    public void setIdentificadorExterno(String identificadorExterno) {
        this.identificadorExterno = identificadorExterno;
    }

    public String getIdentificacionApartamento() {
        return identificacionApartamento;
    }

    public void setIdentificacionApartamento(String identificacionApartamento) {
        this.identificacionApartamento = identificacionApartamento;
    }

    public EstanciaEmbeddable getEstancia() {
        return estancia;
    }

    public void setEstancia(EstanciaEmbeddable estancia) {
        this.estancia = estancia;
    }

    public String getCodigoReservaVigente() {
        return codigoReservaVigente;
    }

    public void setCodigoReservaVigente(String codigoReservaVigente) {
        this.codigoReservaVigente = codigoReservaVigente;
    }

    public LocalDateTime getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDateTime fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public boolean isRevisado() {
        return revisado;
    }

    public void setRevisado(boolean revisado) {
        this.revisado = revisado;
    }

    public String getNotaRevision() {
        return notaRevision;
    }

    public void setNotaRevision(String notaRevision) {
        this.notaRevision = notaRevision;
    }
}
