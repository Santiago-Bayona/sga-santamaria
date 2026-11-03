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

/**
 * Tabla apartamentos. Galeria, dotacion y el historial de tarifas son colecciones. La tarifa vigente se reconstruye reaplicando ese historial.
 */

@Entity
@Table(name = "apartamentos")
public class ApartamentoJpaEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String identificacion;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(nullable = false)
    private int dormitorios;

    @Column(nullable = false)
    private int capacidad;

    @Column(name = "estado_operativo", nullable = false, length = 40)
    private String estadoOperativo;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "indice_principal", nullable = false)
    private int indicePrincipal;

    @ElementCollection
    @CollectionTable(name = "apartamento_imagenes", joinColumns = @JoinColumn(name = "identificacion"))
    @OrderColumn(name = "indice")
    @Column(name = "url", nullable = false, length = 500)
    private List<String> imagenes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "apartamento_dotacion", joinColumns = @JoinColumn(name = "identificacion"))
    @OrderColumn(name = "indice")
    @Column(name = "elemento", nullable = false)
    private List<String> dotacion = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "apartamento_tarifas", joinColumns = @JoinColumn(name = "identificacion"))
    @OrderColumn(name = "indice")
    private List<RegistroTarifaEmbeddable> historialTarifas = new ArrayList<>();

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
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

    public int getDormitorios() {
        return dormitorios;
    }

    public void setDormitorios(int dormitorios) {
        this.dormitorios = dormitorios;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstadoOperativo() {
        return estadoOperativo;
    }

    public void setEstadoOperativo(String estadoOperativo) {
        this.estadoOperativo = estadoOperativo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getIndicePrincipal() {
        return indicePrincipal;
    }

    public void setIndicePrincipal(int indicePrincipal) {
        this.indicePrincipal = indicePrincipal;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }

    public List<String> getDotacion() {
        return dotacion;
    }

    public void setDotacion(List<String> dotacion) {
        this.dotacion = dotacion;
    }

    public List<RegistroTarifaEmbeddable> getHistorialTarifas() {
        return historialTarifas;
    }

    public void setHistorialTarifas(List<RegistroTarifaEmbeddable> historialTarifas) {
        this.historialTarifas = historialTarifas;
    }
}
