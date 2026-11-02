package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.TitularNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService.ResultadoConciliacion;
import co.edu.uniquindio.sga.domain.service.ConciliacionCanalExternoService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoCanalExterno;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ConciliarReservaExternaUseCase {

    private final ConciliacionCanalExternoService conciliacionService;
    private final TitularRepository titularRepository;
    private final ApartamentoRepository apartamentoRepository;

    public ConciliarReservaExternaUseCase(
            ConciliacionCanalExternoService conciliacionService,
            TitularRepository titularRepository,
            ApartamentoRepository apartamentoRepository) {
        this.conciliacionService = conciliacionService;
        this.titularRepository = titularRepository;
        this.apartamentoRepository = apartamentoRepository;
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

        return conciliacionService.conciliar(
                codigoCanal,
                identificador,
                titular,
                identificacion,
                estancia,
                acompanantes,
                horaLlegadaEstimada,
                momentoActual
        );
    }
}