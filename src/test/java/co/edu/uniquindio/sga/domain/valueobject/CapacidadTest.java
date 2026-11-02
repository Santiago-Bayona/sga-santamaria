package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** 3.2 / RN-02: todos los ocupantes cuentan para la capacidad, sean o no facturables; tope rígido. */
class CapacidadTest {

    @Test
    @DisplayName("3.2: el total de ocupantes (facturables o no) hasta la capacidad cabe")
    void admitirHastaLaCapacidad() {
        Capacidad capacidad = new Capacidad(4);

        assertTrue(capacidad.admite(1));
        assertTrue(capacidad.admite(4));
    }

    @Test
    @DisplayName("3.2/RN-02: pasar de la capacidad no cabe, aunque los excedentes sean menores sin cargo")
    void noAdmitirMasOcupantesQueLaCapacidad() {
        assertFalse(new Capacidad(4).admite(5));
    }

    @Test
    @DisplayName("F-03: la capacidad debe admitir al menos una persona")
    void noCrearCapacidadMenorAUno() {
        assertThrows(ReglaDominioException.class, () -> new Capacidad(0));
        assertThrows(ReglaDominioException.class, () -> new Capacidad(-2));
    }
}
