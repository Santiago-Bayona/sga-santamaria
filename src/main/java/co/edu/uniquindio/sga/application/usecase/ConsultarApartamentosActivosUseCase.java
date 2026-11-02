package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.application.exception.SolicitudAplicacionException;
import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultarApartamentosActivosUseCase {

    private final ApartamentoRepository apartamentoRepository;

    public ConsultarApartamentosActivosUseCase(ApartamentoRepository apartamentoRepository) {
        this.apartamentoRepository = apartamentoRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<Apartamento> ejecutar(int pagina) {
        if (pagina < 0) {
            throw new SolicitudAplicacionException("El número de página no puede ser negativo.");
        }
        return apartamentoRepository.buscarActivos(pagina);
    }
}