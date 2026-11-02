package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.TitularYaRegistradoException;
import co.edu.uniquindio.sga.domain.entity.Titular;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoTitular;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class RegistrarTitularUseCase {

    private final TitularRepository titularRepository;

    public RegistrarTitularUseCase(TitularRepository titularRepository) {
        this.titularRepository = titularRepository;
    }

    @Transactional
    public Titular ejecutar(CodigoTitular codigo, String nombre, LocalDate fechaNacimiento, String telefono,
                            String correo) {

        // El código identifica al titular: no puede haber dos con el mismo.
        if (titularRepository.obtenerPorCodigo(codigo).isPresent()) {
            throw new TitularYaRegistradoException(codigo);
        }

        // Las reglas del titular (nombre, fecha, teléfono, correo) las valida el constructor del dominio.
        Titular titular = new Titular(codigo, nombre, fechaNacimiento, telefono, correo);
        titularRepository.guardar(titular);
        return titular;
    }
}
