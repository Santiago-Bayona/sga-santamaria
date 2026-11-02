package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.TitularNoEncontradoException;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarTitularUseCase {

    private final TitularRepository titularRepository;

    public ActualizarTitularUseCase(TitularRepository titularRepository) {
        this.titularRepository = titularRepository;
    }

    @Transactional
    public Titular ejecutar(CodigoTitular codigo, String telefono, String correo) {

        Titular titular = titularRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new TitularNoEncontradoException(codigo));

        // Cambiar el contacto no cambia la identidad del titular.
        titular.actualizarContacto(telefono, correo);
        titularRepository.guardar(titular);
        return titular;
    }
}
