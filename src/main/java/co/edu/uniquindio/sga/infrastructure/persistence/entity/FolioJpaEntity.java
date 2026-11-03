package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabla folios. Cargos y pagos son colecciones ordenadas por índice.
 */

@Entity
@Table(name = "folios", indexes = @Index(name = "idx_folio_reserva", columnList = "codigo_reserva", unique = true))
public class FolioJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String codigo;

    @Column(name = "codigo_reserva", nullable = false, unique = true, length = 64)
    private String codigoReserva;

    @Column(nullable = false)
    private boolean cerrado;

    @Column(name = "autor_cierre")
    private String autorCierre;

    @Column(name = "motivo_cierre", length = 500)
    private String motivoCierre;

    @ElementCollection
    @CollectionTable(name = "folio_cargos", joinColumns = @JoinColumn(name = "codigo_folio"))
    @OrderColumn(name = "indice")
    private List<CargoEmbeddable> cargos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "folio_pagos", joinColumns = @JoinColumn(name = "codigo_folio"))
    @OrderColumn(name = "indice")
    private List<PagoEmbeddable> pagos = new ArrayList<>();

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

    public boolean isCerrado() {
        return cerrado;
    }

    public void setCerrado(boolean cerrado) {
        this.cerrado = cerrado;
    }

    public String getAutorCierre() {
        return autorCierre;
    }

    public void setAutorCierre(String autorCierre) {
        this.autorCierre = autorCierre;
    }

    public String getMotivoCierre() {
        return motivoCierre;
    }

    public void setMotivoCierre(String motivoCierre) {
        this.motivoCierre = motivoCierre;
    }

    public List<CargoEmbeddable> getCargos() {
        return cargos;
    }

    public void setCargos(List<CargoEmbeddable> cargos) {
        this.cargos = cargos;
    }

    public List<PagoEmbeddable> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoEmbeddable> pagos) {
        this.pagos = pagos;
    }
}
