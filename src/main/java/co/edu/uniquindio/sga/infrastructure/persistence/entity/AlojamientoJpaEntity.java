package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Tabla del alojamiento. Temporadas, periodos, normas y servicios son colecciones. Los periodos no van anidados dentro de la temporada.
 */

@Entity
@Table(name = "alojamientos")
public class AlojamientoJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(nullable = false)
    private String ciudad;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada;

    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @Column(name = "tiempo_preparacion_minutos", nullable = false)
    private long tiempoPreparacionMinutos;

    @Column(name = "plazo_confirmacion_horas", nullable = false)
    private int plazoConfirmacionHoras;

    @Column(name = "hora_limite_noshow", nullable = false)
    private LocalTime horaLimiteNoShow;

    @Column(name = "umbral_edad_facturable", nullable = false)
    private int umbralEdadFacturable;

    @Column(name = "porcentaje_anticipo", nullable = false)
    private int porcentajeAnticipo;

    @Column(name = "antelacion_recordatorio_entrada_horas", nullable = false)
    private long antelacionRecordatorioEntradaHoras;

    @Column(name = "antelacion_recordatorio_salida_horas", nullable = false)
    private long antelacionRecordatorioSalidaHoras;

    @ElementCollection
    @CollectionTable(name = "alojamiento_temporadas", joinColumns = @JoinColumn(name = "codigo_alojamiento"))
    @OrderColumn(name = "indice")
    private List<TemporadaEmbeddable> temporadas = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "alojamiento_periodos", joinColumns = @JoinColumn(name = "codigo_alojamiento"))
    @OrderColumn(name = "indice")
    private List<PeriodoTemporadaEmbeddable> periodos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "alojamiento_normas", joinColumns = @JoinColumn(name = "codigo_alojamiento"))
    @OrderColumn(name = "indice")
    @Column(name = "texto", nullable = false, length = 500)
    private List<String> normas = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "alojamiento_servicios", joinColumns = @JoinColumn(name = "codigo_alojamiento"))
    @OrderColumn(name = "indice")
    private List<ServicioEmbeddable> servicios = new ArrayList<>();

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public long getTiempoPreparacionMinutos() {
        return tiempoPreparacionMinutos;
    }

    public void setTiempoPreparacionMinutos(long tiempoPreparacionMinutos) {
        this.tiempoPreparacionMinutos = tiempoPreparacionMinutos;
    }

    public int getPlazoConfirmacionHoras() {
        return plazoConfirmacionHoras;
    }

    public void setPlazoConfirmacionHoras(int plazoConfirmacionHoras) {
        this.plazoConfirmacionHoras = plazoConfirmacionHoras;
    }

    public LocalTime getHoraLimiteNoShow() {
        return horaLimiteNoShow;
    }

    public void setHoraLimiteNoShow(LocalTime horaLimiteNoShow) {
        this.horaLimiteNoShow = horaLimiteNoShow;
    }

    public int getUmbralEdadFacturable() {
        return umbralEdadFacturable;
    }

    public void setUmbralEdadFacturable(int umbralEdadFacturable) {
        this.umbralEdadFacturable = umbralEdadFacturable;
    }

    public int getPorcentajeAnticipo() {
        return porcentajeAnticipo;
    }

    public void setPorcentajeAnticipo(int porcentajeAnticipo) {
        this.porcentajeAnticipo = porcentajeAnticipo;
    }

    public long getAntelacionRecordatorioEntradaHoras() {
        return antelacionRecordatorioEntradaHoras;
    }

    public void setAntelacionRecordatorioEntradaHoras(long antelacionRecordatorioEntradaHoras) {
        this.antelacionRecordatorioEntradaHoras = antelacionRecordatorioEntradaHoras;
    }

    public long getAntelacionRecordatorioSalidaHoras() {
        return antelacionRecordatorioSalidaHoras;
    }

    public void setAntelacionRecordatorioSalidaHoras(long antelacionRecordatorioSalidaHoras) {
        this.antelacionRecordatorioSalidaHoras = antelacionRecordatorioSalidaHoras;
    }

    public List<TemporadaEmbeddable> getTemporadas() {
        return temporadas;
    }

    public void setTemporadas(List<TemporadaEmbeddable> temporadas) {
        this.temporadas = temporadas;
    }

    public List<PeriodoTemporadaEmbeddable> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<PeriodoTemporadaEmbeddable> periodos) {
        this.periodos = periodos;
    }

    public List<String> getNormas() {
        return normas;
    }

    public void setNormas(List<String> normas) {
        this.normas = normas;
    }

    public List<ServicioEmbeddable> getServicios() {
        return servicios;
    }

    public void setServicios(List<ServicioEmbeddable> servicios) {
        this.servicios = servicios;
    }
}
