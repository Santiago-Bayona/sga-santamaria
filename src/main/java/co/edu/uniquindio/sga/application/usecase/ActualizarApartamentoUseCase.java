package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.GaleriaImagenes;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActualizarApartamentoUseCase {

    private final ApartamentoRepository apartamentoRepository;

    public ActualizarApartamentoUseCase(ApartamentoRepository apartamentoRepository) {
        this.apartamentoRepository = apartamentoRepository;
    }

    @Transactional
    public Apartamento ejecutar(IdentificacionApartamento identificacion, String nombre, String descripcion,
                                int dormitorios, GaleriaImagenes galeria, List<String> dotacion) {

        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(identificacion)
                .orElseThrow(() -> new ApartamentoNoEncontradoException(identificacion));

        apartamento.actualizarDatos(nombre, descripcion, dormitorios);
        apartamento.reemplazarGaleria(galeria);
        apartamento.definirDotacion(dotacion);

        apartamentoRepository.guardar(apartamento);
        return apartamento;
    }
}
