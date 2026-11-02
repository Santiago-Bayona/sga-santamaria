package co.edu.uniquindio.sga.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static co.edu.uniquindio.sga.domain.valueobject.EstadoReserva.*;
import static org.junit.jupiter.api.Assertions.*;

/** RN-08: tabla completa de transiciones permitidas (7.5); todo par que no esté en la tabla es inválido. */
class EstadoReservaTest {

    @Test
    @DisplayName("RN-08: solo las transiciones del diagrama de estados son válidas; las demás, ninguna")
    void verificarTransitoEstadoInvalido() {
        Map<EstadoReserva, Set<EstadoReserva>> permitidas = new EnumMap<>(EstadoReserva.class);
        permitidas.put(PENDIENTE, EnumSet.of(CONFIRMADA, CANCELADA));
        permitidas.put(CONFIRMADA, EnumSet.of(EN_CURSO, CANCELADA, NO_SHOW));
        permitidas.put(EN_CURSO, EnumSet.of(FINALIZADA));
        permitidas.put(FINALIZADA, EnumSet.noneOf(EstadoReserva.class));
        permitidas.put(CANCELADA, EnumSet.noneOf(EstadoReserva.class));
        permitidas.put(NO_SHOW, EnumSet.noneOf(EstadoReserva.class));

        for (EstadoReserva origen : EstadoReserva.values()) {
            for (EstadoReserva destino : EstadoReserva.values()) {
                assertEquals(permitidas.get(origen).contains(destino), origen.puedeTransitarA(destino),
                        origen + " → " + destino);
            }
        }
    }

    @Test
    @DisplayName("RN-08/RN-12: activas = PENDIENTE, CONFIRMADA y EN_CURSO; el resto son terminales")
    void distinguirEstadosActivosDeTerminales() {
        assertTrue(PENDIENTE.esActiva() && CONFIRMADA.esActiva() && EN_CURSO.esActiva());
        assertTrue(FINALIZADA.esTerminal() && CANCELADA.esTerminal() && NO_SHOW.esTerminal());
    }
}
