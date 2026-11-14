package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.ResultadoMensajeCanal;

import java.time.LocalDateTime;
import java.util.Objects;

/** Un mensaje recibido de un canal externo, con su resultado. Se conserva aunque sea un duplicado. */
public class EventoBitacoraCanal {

    private final String codigo;
    private final String codigoCanal;
    private final String identificadorExterno;
    private final LocalDateTime ocurridoEn;
    private final String payload;
    private final ResultadoMensajeCanal resultado;
    private final String motivo;

    public EventoBitacoraCanal(String codigo, String codigoCanal, String identificadorExterno,
                               LocalDateTime ocurridoEn, String payload, ResultadoMensajeCanal resultado,
                               String motivo) {
        if (codigo == null || codigo.isBlank() || codigoCanal == null || codigoCanal.isBlank()
                || identificadorExterno == null || identificadorExterno.isBlank()
                || ocurridoEn == null || payload == null || payload.isBlank()
                || resultado == null || motivo == null || motivo.isBlank()) {
            throw new ReglaDominioException("El evento de bitácora requiere canal, identificador, momento, payload y resultado.");
        }
        this.codigo = codigo.trim();
        this.codigoCanal = codigoCanal.trim();
        this.identificadorExterno = identificadorExterno.trim();
        this.ocurridoEn = ocurridoEn;
        this.payload = payload.length() > 500 ? payload.substring(0, 500) : payload;
        this.resultado = resultado;
        this.motivo = motivo.length() > 500 ? motivo.substring(0, 500) : motivo.trim();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCodigoCanal() {
        return codigoCanal;
    }

    public String getIdentificadorExterno() {
        return identificadorExterno;
    }

    public LocalDateTime getOcurridoEn() {
        return ocurridoEn;
    }

    public String getPayload() {
        return payload;
    }

    public ResultadoMensajeCanal getResultado() {
        return resultado;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventoBitacoraCanal that)) return false;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
