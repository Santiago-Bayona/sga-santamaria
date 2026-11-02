package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.ApartamentoNoEncontradoException;
import co.edu.uniquindio.sga.application.exception.NovedadYaRegistradaException;
import co.edu.uniquindio.sga.domain.entity.Novedad;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.NovedadRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoNovedad;
import co.edu.uniquindio.sga.domain.valueobject.GravedadNovedad;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrarNovedadUseCase {

    private final NovedadRepository novedadRepository;
    private final ApartamentoRepository apartamentoRepository;

    public RegistrarNovedadUseCase(
            NovedadRepository novedadRepository,
            ApartamentoRepository apartamentoRepository) {
        this.novedadRepository = novedadRepository;
        this.apartamentoRepository = apartamentoRepository;
    }

    /**
     * @param autor quien reporta la novedad; lo resuelve infraestructura desde la sesión autenticada
     */
    @Transactional
    public Novedad ejecutar(CodigoNovedad codigo, IdentificacionApartamento identificacion, String descripcion,
                            GravedadNovedad gravedad, String autor,
                            LocalDateTime momentoActual) {

        if (novedadRepository.obtenerPorCodigo(codigo).isPresent()) {
            throw new NovedadYaRegistradaException(codigo.valor());
        }

        apartamentoRepository.obtenerPorIdentificacion(identificacion)
                .orElseThrow(() -> new ApartamentoNoEncontradoException(identificacion));

        Novedad novedad = new Novedad(codigo, identificacion, momentoActual, autor, descripcion, gravedad);

        novedadRepository.guardar(novedad);
        return novedad;
    }
}