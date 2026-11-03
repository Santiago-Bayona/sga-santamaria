package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoYaRegistradoException;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.Capacidad;
import co.edu.uniquindio.sga.domain.valueobject.GaleriaImagenes;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CrearApartamentoUseCase {

    private final ApartamentoRepository apartamentoRepository;

    public CrearApartamentoUseCase(ApartamentoRepository apartamentoRepository) {
        this.apartamentoRepository = apartamentoRepository;
    }

    @Transactional
    public Apartamento ejecutar(IdentificacionApartamento identificacion, String nombre, String descripcion,
                                int dormitorios, Capacidad capacidad, GaleriaImagenes galeria,
                                List<String> dotacion) {

        // La identificación es única.
        if (apartamentoRepository.obtenerPorIdentificacion(identificacion).isPresent()) {
            throw new ApartamentoYaRegistradoException(identificacion);
        }

        // Nace inactivo (7.4): se activa después, cuando tenga tarifas en todas las temporadas.
        Apartamento apartamento = new Apartamento(identificacion, nombre, descripcion, dormitorios, capacidad, galeria);
        apartamento.definirDotacion(dotacion);

        apartamentoRepository.guardar(apartamento);
        return apartamento;
    }
}
