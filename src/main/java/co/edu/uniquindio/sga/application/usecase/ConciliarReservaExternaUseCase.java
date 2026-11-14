package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.TitularNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.EventoBitacoraCanal;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.EventoBitacoraCanalRepository;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService.ResultadoConciliacion;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService.TipoResultado;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import co.edu.uniquindio.sga.domain.valueobject.ResultadoMensajeCanal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConciliarReservaExternaUseCase {

    private final ConciliacionCanalExternoService conciliacionService;
    private final TitularRepository titularRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final EventoBitacoraCanalRepository bitacora;

    public ConciliarReservaExternaUseCase(
            ConciliacionCanalExternoService conciliacionService,
            TitularRepository titularRepository,
            ApartamentoRepository apartamentoRepository,
            EventoBitacoraCanalRepository bitacora) {
        this.conciliacionService = conciliacionService;
        this.titularRepository = titularRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.bitacora = bitacora;
    }

    @Transactional
    public ResultadoConciliacion ejecutar(CodigoCanalExterno codigoCanal, IdentificadorExterno identificador,
                                          CodigoTitular codigoTitular, IdentificacionApartamento identificacion,
                                          Estancia estancia, List<Ocupante> acompanantes,
                                          LocalTime horaLlegadaEstimada,
                            LocalDateTime momentoActual) {

        Titular titular = titularRepository.obtenerPorCodigo(codigoTitular)
                .orElseThrow(() -> new TitularNoEncontradoException(codigoTitular));

        apartamentoRepository.obtenerPorIdentificacion(identificacion)
                .orElseThrow(() -> new ApartamentoNoEncontradoException(identificacion));

        ResultadoConciliacion resultado = conciliacionService.conciliar(
                codigoCanal,
                identificador,
                titular,
                identificacion,
                estancia,
                acompanantes,
                horaLlegadaEstimada,
                momentoActual
        );
        bitacora.guardar(evento(resultado, codigoCanal, identificador, identificacion, estancia, codigoTitular, momentoActual));
        return resultado;
    }

    /** Anota cada mensaje, también el repetido. La hora que llega ya está en America/Bogota. */
    private static EventoBitacoraCanal evento(ResultadoConciliacion resultado, CodigoCanalExterno canal,
                                             IdentificadorExterno identificador, IdentificacionApartamento apartamento,
                                             Estancia estancia, CodigoTitular titular, LocalDateTime momento) {
        ResultadoMensajeCanal tipo;
        String motivo;
        if (resultado.tipo() == TipoResultado.ACEPTADA && resultado.primeraVez()) {
            tipo = ResultadoMensajeCanal.ACEPTADA;
            motivo = "Se creó la reserva " + resultado.reserva().getCodigo().valor();
        } else if (resultado.tipo() == TipoResultado.CONFLICTO && resultado.primeraVez()) {
            tipo = ResultadoMensajeCanal.RECHAZADA_CONFLICTO;
            motivo = "Se solapa con la reserva vigente " + resultado.conflicto().getReservaVigente().valor();
        } else {
            tipo = ResultadoMensajeCanal.DUPLICADO;
            motivo = "El identificador externo ya fue procesado; no se creó otra reserva.";
        }
        String payload = apartamento.valor() + " " + estancia.entrada() + "/" + estancia.salida()
                + " titular " + titular.valor();
        return new EventoBitacoraCanal("EVT-" + UUID.randomUUID(), canal.valor(), identificador.valor(),
                momento, payload, tipo, motivo);
    }
}