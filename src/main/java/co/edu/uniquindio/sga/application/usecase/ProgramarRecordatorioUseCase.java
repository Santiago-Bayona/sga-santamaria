package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Recordatorio;
import co.edu.uniquindio.sga.domain.service.GestionRecordatoriosService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.TipoRecordatorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgramarRecordatorioUseCase {

    private final GestionRecordatoriosService gestionRecordatoriosService;

    public ProgramarRecordatorioUseCase(GestionRecordatoriosService gestionRecordatoriosService) {
        this.gestionRecordatoriosService = gestionRecordatoriosService;
    }

    @Transactional
    public Recordatorio ejecutar(CodigoReserva codigoReserva, TipoRecordatorio tipo) {

        return gestionRecordatoriosService.programarRecordatorio(codigoReserva, tipo);
    }
}