package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.service.ModificacionReservaService;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Estancia;
import co.edu.uniquindio.sga.domain.valueobject.IdentificacionApartamento;
import co.edu.uniquindio.sga.domain.valueobject.Mascotas;
import co.edu.uniquindio.sga.domain.valueobject.Ocupante;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModificarReservaUseCase {

    private final ModificacionReservaService modificacionReservaService;

    public ModificarReservaUseCase(ModificacionReservaService modificacionReservaService) {

        this.modificacionReservaService = modificacionReservaService;
    }

    /**
     * @param mascotas nueva cantidad de mascotas; {@code null} si no cambia
     */
    @Transactional
    public BigDecimal ejecutar(CodigoReserva codigoReserva, IdentificacionApartamento apartamento,
                               Estancia nuevaEstancia, List<Ocupante> nuevosAcompanantes, Mascotas mascotas,
                            LocalDateTime momentoActual) {

        return modificacionReservaService.modificar(
                codigoReserva,
                apartamento,
                nuevaEstancia,
                nuevosAcompanantes,
                mascotas,
                momentoActual
        );
    }

}
