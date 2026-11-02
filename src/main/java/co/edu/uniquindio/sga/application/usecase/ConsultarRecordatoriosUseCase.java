package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Recordatorio;
import co.edu.uniquindio.sga.domain.repository.RecordatorioRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultarRecordatoriosUseCase {

    private final RecordatorioRepository recordatorioRepository;

    public ConsultarRecordatoriosUseCase(RecordatorioRepository recordatorioRepository) {
        this.recordatorioRepository = recordatorioRepository;
    }

    /** A lo sumo hay un recordatorio de entrada y uno de salida por reserva (RN-25): no se pagina. */
    @Transactional(readOnly = true)
    public List<Recordatorio> ejecutar(CodigoReserva codigo) {

        return recordatorioRepository.buscarPorReserva(codigo);
    }
}