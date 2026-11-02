package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Novedad;
import co.edu.uniquindio.sga.domain.repository.NovedadRepository;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarNovedadesUseCase {

    private final NovedadRepository novedadRepository;

    public ConsultarNovedadesUseCase(NovedadRepository novedadRepository) {
        this.novedadRepository = novedadRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<Novedad> ejecutar(IdentificacionApartamento identificacion, int pagina) {

        return PaginacionEnMemoria.de(novedadRepository.buscarPorApartamento(identificacion), pagina);
    }
}