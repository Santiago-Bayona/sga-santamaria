package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Recordatorio programado, enviado o cancelado, ligado a una reserva.
 */

@Entity
@Table(name = "recordatorios")
public class RecordatorioJpaEntity {

    @Id
    @Column(nullable = false, length = 120)
    private String codigo;

    @Column(name = "codigo_reserva", nullable = false, length = 64)
    private String codigoReserva;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(name = "momento_evento", nullable = false)
    private LocalDateTime momentoEvento;

    @Column(name = "fecha_recordatorio", nullable = false)
    private LocalDateTime fechaRecordatorio;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "ultimo_error", length = 1000)
    private String ultimoError;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getMomentoEvento() {
        return momentoEvento;
    }

    public void setMomentoEvento(LocalDateTime momentoEvento) {
        this.momentoEvento = momentoEvento;
    }

    public LocalDateTime getFechaRecordatorio() {
        return fechaRecordatorio;
    }

    public void setFechaRecordatorio(LocalDateTime fechaRecordatorio) {
        this.fechaRecordatorio = fechaRecordatorio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    public void setUltimoError(String ultimoError) {
        this.ultimoError = ultimoError;
    }
}
