package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "bitacora_canal")
public class EventoBitacoraCanalJpaEntity {

    @Id
    @Column(nullable = false, length = 80)
    private String codigo;

    @Column(name = "codigo_canal", nullable = false, length = 64)
    private String codigoCanal;

    @Column(name = "identificador_externo", nullable = false, length = 120)
    private String identificadorExterno;

    @Column(name = "ocurrido_en", nullable = false)
    private LocalDateTime ocurridoEn;

    @Column(nullable = false, length = 500)
    private String payload;

    @Column(nullable = false, length = 30)
    private String resultado;

    @Column(nullable = false, length = 500)
    private String motivo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoCanal() {
        return codigoCanal;
    }

    public void setCodigoCanal(String codigoCanal) {
        this.codigoCanal = codigoCanal;
    }

    public String getIdentificadorExterno() {
        return identificadorExterno;
    }

    public void setIdentificadorExterno(String identificadorExterno) {
        this.identificadorExterno = identificadorExterno;
    }

    public LocalDateTime getOcurridoEn() {
        return ocurridoEn;
    }

    public void setOcurridoEn(LocalDateTime ocurridoEn) {
        this.ocurridoEn = ocurridoEn;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
