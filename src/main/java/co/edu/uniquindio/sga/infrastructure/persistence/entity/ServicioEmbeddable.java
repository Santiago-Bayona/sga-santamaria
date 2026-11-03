package co.edu.uniquindio.sga.infrastructure.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.util.Objects;

/**
 * Servicio adicional del alojamiento, con precio y si genera cargo en el folio.
 */

@Embeddable
public class ServicioEmbeddable {

    @Column(nullable = false)
    private String nombre;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "valor", column = @Column(name = "valor", nullable = false, precision = 19, scale = 0))
    })
    private DineroEmbeddable valor;

    @Column(name = "genera_cargo", nullable = false)
    private boolean generaCargo;

    public ServicioEmbeddable() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public DineroEmbeddable getValor() {
        return valor;
    }

    public void setValor(DineroEmbeddable valor) {
        this.valor = valor;
    }

    public boolean isGeneraCargo() {
        return generaCargo;
    }

    public void setGeneraCargo(boolean generaCargo) {
        this.generaCargo = generaCargo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServicioEmbeddable otro)) {
            return false;
        }
        return generaCargo == otro.generaCargo && Objects.equals(nombre, otro.nombre) && Objects.equals(valor, otro.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, valor, generaCargo);
    }
}
