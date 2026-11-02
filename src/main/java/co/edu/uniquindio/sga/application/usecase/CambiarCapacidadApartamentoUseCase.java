package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.service.CambioCapacidadApartamentoService;
import co.edu.uniquindio.sga.domain.valueobject.Capacidad;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CambiarCapacidadApartamentoUseCase {

    private final CambioCapacidadApartamentoService cambioCapacidadApartamentoService;

    public CambiarCapacidadApartamentoUseCase(CambioCapacidadApartamentoService cambioCapacidadApartamentoService) {
        this.cambioCapacidadApartamentoService = cambioCapacidadApartamentoService;
    }

    @Transactional
    public List<Reserva> ejecutar(IdentificacionApartamento identificacion, Capacidad nuevaCapacidad) {

        return cambioCapacidadApartamentoService.cambiarCapacidad(identificacion, nuevaCapacidad);
    }
}