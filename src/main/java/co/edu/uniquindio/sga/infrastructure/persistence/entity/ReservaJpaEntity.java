package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservas", indexes = {
        @Index(name = "idx_reserva_estado", columnList = "estado"),
        @Index(name = "idx_reserva_apartamento", columnList = "identificacion_apartamento"),
        @Index(name = "idx_reserva_titular", columnList = "codigo_titular"),
        @Index(name = "idx_reserva_ext", columnList = "identificador_externo")
})
/**
 * Tabla reservas. La estancia va embebida y ocupantes y desglose van en tablas de colección, con índice para conservar el orden.
 */
public class ReservaJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String codigo;

    @Column(name = "codigo_titular", nullable = false, length = 64)
    private String codigoTitular;

    @Column(name = "nombre_titular", nullable = false)
    private String nombreTitular;

    @Column(name = "nacimiento_titular", nullable = false)
    private LocalDate nacimientoTitular;

    @Column(name = "version_politica", nullable = false)
    private int versionPolitica;

    @Column(name = "canal_origen", nullable = false, length = 20)
    private String canalOrigen;

    @Column(name = "identificador_externo", length = 120)
    private String identificadorExterno;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "identificacion_apartamento", nullable = false, length = 64)
    private String identificacionApartamento;

    @Embedded
    private EstanciaEmbeddable estancia;

    @Column(name = "hora_llegada_estimada")
    private LocalTime horaLlegadaEstimada;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "mascotas", nullable = false)
    private int mascotas;

    @Column(name = "momento_registro")
    private LocalDateTime momentoRegistro;

    @Column(name = "momento_salida")
    private LocalDateTime momentoSalida;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @ElementCollection
    @CollectionTable(name = "reserva_ocupantes", joinColumns = @JoinColumn(name = "codigo_reserva"))
    @OrderColumn(name = "indice")
    private List<OcupanteEmbeddable> ocupantes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "reserva_desglose", joinColumns = @JoinColumn(name = "codigo_reserva"))
    @OrderColumn(name = "indice")
    private List<DesgloseNocheEmbeddable> desglose = new ArrayList<>();

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoTitular() {
        return codigoTitular;
    }

    public void setCodigoTitular(String codigoTitular) {
        this.codigoTitular = codigoTitular;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public LocalDate getNacimientoTitular() {
        return nacimientoTitular;
    }

    public void setNacimientoTitular(LocalDate nacimientoTitular) {
        this.nacimientoTitular = nacimientoTitular;
    }

    public int getVersionPolitica() {
        return versionPolitica;
    }

    public void setVersionPolitica(int versionPolitica) {
        this.versionPolitica = versionPolitica;
    }

    public String getCanalOrigen() {
        return canalOrigen;
    }

    public void setCanalOrigen(String canalOrigen) {
        this.canalOrigen = canalOrigen;
    }

    public int getMascotas() {
        return mascotas;
    }

    public void setMascotas(int mascotas) {
        this.mascotas = mascotas;
    }

    public String getIdentificadorExterno() {
        return identificadorExterno;
    }

    public void setIdentificadorExterno(String identificadorExterno) {
        this.identificadorExterno = identificadorExterno;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public LocalTime getHoraLlegadaEstimada() {
        return horaLlegadaEstimada;
    }

    public void setHoraLlegadaEstimada(LocalTime horaLlegadaEstimada) {
        this.horaLlegadaEstimada = horaLlegadaEstimada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getMomentoRegistro() {
        return momentoRegistro;
    }

    public void setMomentoRegistro(LocalDateTime momentoRegistro) {
        this.momentoRegistro = momentoRegistro;
    }

    public LocalDateTime getMomentoSalida() {
        return momentoSalida;
    }

    public void setMomentoSalida(LocalDateTime momentoSalida) {
        this.momentoSalida = momentoSalida;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public List<OcupanteEmbeddable> getOcupantes() {
        return ocupantes;
    }

    public void setOcupantes(List<OcupanteEmbeddable> ocupantes) {
        this.ocupantes = ocupantes;
    }

    public List<DesgloseNocheEmbeddable> getDesglose() {
        return desglose;
    }

    public void setDesglose(List<DesgloseNocheEmbeddable> desglose) {
        this.desglose = desglose;
    }
}
