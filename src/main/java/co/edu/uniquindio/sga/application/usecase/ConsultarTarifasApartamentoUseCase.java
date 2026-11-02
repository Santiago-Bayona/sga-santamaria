package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Tarifa;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Tarifas vigentes de un apartamento, una por temporada (7.4). */

@Service
public class ConsultarTarifasApartamentoUseCase {

    private final ApartamentoRepository apartamentoRepository;

    public ConsultarTarifasApartamentoUseCase(ApartamentoRepository apartamentoRepository) {
        this.apartamentoRepository = apartamentoRepository;
    }

    /** Las tarifas de un apartamento son un conjunto pequeño y acotado por sus temporadas: no se pagina. */
    @Transactional(readOnly = true)
    public List<Tarifa> ejecutar(IdentificacionApartamento identificacion) {

        return apartamentoRepository.obtenerPorIdentificacion(identificacion)
                .orElseThrow(() -> new ApartamentoNoEncontradoException(identificacion))
                .getTarifas();
    }
}
