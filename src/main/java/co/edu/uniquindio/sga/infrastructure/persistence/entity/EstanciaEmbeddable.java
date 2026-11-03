package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.util.Objects;

/** Rango [entrada, salida) persistido. En bloqueos las columnas se renombran. */
@Embeddable
public class EstanciaEmbeddable {

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    public EstanciaEmbeddable() {
    }

    public EstanciaEmbeddable(LocalDate fechaEntrada, LocalDate fechaSalida) {
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EstanciaEmbeddable otro)) {
            return false;
        }
        return Objects.equals(fechaEntrada, otro.fechaEntrada) && Objects.equals(fechaSalida, otro.fechaSalida);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fechaEntrada, fechaSalida);
    }
}
