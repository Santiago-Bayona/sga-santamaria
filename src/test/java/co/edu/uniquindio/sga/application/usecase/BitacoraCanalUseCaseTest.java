package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.CanalExterno;
import co.edu.uniquindio.sga.domain.entity.EventoBitacoraCanal;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.repository.EventoBitacoraCanalRepository;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService;
import co.edu.uniquindio.sga.domain.soporte.ApartamentoRepositoryEnMemoria;
import co.edu.uniquindio.sga.domain.soporte.CanalExternoRepositoryEnMemoria;
import co.edu.uniquindio.sga.domain.soporte.ConflictoCanalRepositoryEnMemoria;
import co.edu.uniquindio.sga.domain.soporte.DatosDePrueba;
import co.edu.uniquindio.sga.domain.soporte.EscenarioReservas;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import co.edu.uniquindio.sga.domain.valueobject.ResultadoMensajeCanal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 10.3.1 / 7.8: aceptada, conflicto y mensaje repetido quedan en la bitácora sin duplicar la reserva. */
class BitacoraCanalUseCaseTest {

    private static final CodigoCanalExterno CANAL = new CodigoCanalExterno("BOOKING");

    private EscenarioReservas escenario;
    private BitacoraEnMemoria bitacora;
    private ConciliarReservaExternaUseCase conciliar;

    @BeforeEach
    void preparar() {
        escenario = new EscenarioReservas();
        bitacora = new BitacoraEnMemoria();
        ConciliacionCanalExternoService servicio = new ConciliacionCanalExternoService(
                escenario.reservas, new ConflictoCanalRepositoryEnMemoria(),
                new CanalExternoRepositoryEnMemoria().con(new CanalExterno(CANAL, "OTA", "secreto")),
                escenario.creacion, escenario.cancelacion);
        conciliar = new ConciliarReservaExternaUseCase(servicio, new TitularUnico(),
                new ApartamentoRepositoryEnMemoria().con(escenario.apartamento), bitacora);
    }

    @Test
    @DisplayName("los tres escenarios dejan ACEPTADA, RECHAZADA_CONFLICTO y DUPLICADO, y una sola reserva")
    void tresEscenarios() {
        enviar("MSG-1001");
        enviar("MSG-1002");
        enviar("MSG-1001");

        assertEquals(1, escenario.reservas.buscarPorEstado(EstadoReserva.PENDIENTE).size());
        assertEquals(List.of(
                ResultadoMensajeCanal.ACEPTADA,
                ResultadoMensajeCanal.RECHAZADA_CONFLICTO,
                ResultadoMensajeCanal.DUPLICADO),
                bitacora.eventos.stream().map(EventoBitacoraCanal::getResultado).toList());

        Pagina<EventoBitacoraCanal> consulta = new ConsultarBitacoraCanalUseCase(bitacora).ejecutar(0);
        assertEquals(3, consulta.total());
        assertEquals(ResultadoMensajeCanal.DUPLICADO, consulta.contenido().get(0).getResultado());
    }

    private void enviar(String identificador) {
        conciliar.ejecutar(CANAL, new IdentificadorExterno(identificador), new CodigoTitular("TIT-1"),
                EscenarioReservas.APTO, escenario.estancia(), List.of(), LocalTime.of(15, 0), DatosDePrueba.AHORA);
    }

    private static final class TitularUnico implements TitularRepository {
        @Override
        public Optional<Titular> obtenerPorCodigo(CodigoTitular codigo) {
            return "TIT-1".equals(codigo.valor()) ? Optional.of(DatosDePrueba.titular()) : Optional.empty();
        }

        @Override
        public void guardar(Titular titular) {
        }
    }

    private static final class BitacoraEnMemoria implements EventoBitacoraCanalRepository {
        private final List<EventoBitacoraCanal> eventos = new ArrayList<>();

        @Override
        public void guardar(EventoBitacoraCanal evento) {
            eventos.add(evento);
        }

        @Override
        public Pagina<EventoBitacoraCanal> listar(int pagina) {
            List<EventoBitacoraCanal> recientePrimero = new ArrayList<>(eventos);
            java.util.Collections.reverse(recientePrimero);
            return new Pagina<>(recientePrimero, pagina, recientePrimero.size());
        }
    }
}
