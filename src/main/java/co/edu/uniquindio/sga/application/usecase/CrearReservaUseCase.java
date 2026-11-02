package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.TitularNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.service.CreacionReservaService;
import co.edu.uniquindio.sga.domain.valueobject.CanalOrigen;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.IdentificadorExterno;
import co.edu.uniquindio.sga.domain.valueobject.Mascotas;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CrearReservaUseCase {

    private final CreacionReservaService creacionReservaService;
    private final TitularRepository titularRepository;

    public CrearReservaUseCase(CreacionReservaService creacionReservaService, TitularRepository titularRepository) {

        this.creacionReservaService = creacionReservaService;
        this.titularRepository = titularRepository;
    }

    @Transactional
    public Reserva ejecutar(CodigoTitular codigoTitular, IdentificacionApartamento identificacionApartamento,
                            Estancia estancia, List<Ocupante> acompanantes, Mascotas mascotas,
                            LocalTime horaLlegadaEstimada, CanalOrigen canal,
                            IdentificadorExterno identificadorExterno,
                            LocalDateTime momentoActual) {

        Titular titular = titularRepository
                .obtenerPorCodigo(codigoTitular)
                .orElseThrow(() -> new TitularNoEncontradoException(codigoTitular));

        return creacionReservaService.crear(
                titular,
                identificacionApartamento,
                estancia,
                acompanantes,
                mascotas,
                horaLlegadaEstimada,
                canal,
                identificadorExterno,
                momentoActual
        );
    }
}
