package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Ocupante de la reserva: nombre y fecha de nacimiento. El orden lo guarda la columna índice.
 */

@Embeddable
public class OcupanteEmbeddable {

    @Column(nullable = false)
    private String nombre;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    public OcupanteEmbeddable() {
    }

    public OcupanteEmbeddable(String nombre, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OcupanteEmbeddable otro)) {
            return false;
        }
        return Objects.equals(nombre, otro.nombre) && Objects.equals(fechaNacimiento, otro.fechaNacimiento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, fechaNacimiento);
    }
}
