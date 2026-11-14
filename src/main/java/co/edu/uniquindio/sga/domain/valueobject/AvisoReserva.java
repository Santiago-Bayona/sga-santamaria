package co.edu.uniquindio.sga.domain.valueobject;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;

import java.time.LocalDateTime;

/**
 * Lo que se le cuenta al titular sobre su reserva. Solo datos: cómo se redacta y por qué canal sale lo decide la
 * infraestructura.
 *
 * @param anticipo           valor del anticipo exigido; solo en CREADA (nulo si el alojamiento no lo exige)
 * @param limiteConfirmacion hasta cuándo puede pagarse el anticipo; solo en CREADA
 * @param retencion          lo que se retiene según la política; solo en CANCELADA
 */
public record AvisoReserva(TipoAvisoReserva tipo, CodigoReserva reserva, String nombreTitular, String correo,
                           String telefono, String apartamento, Estancia estancia, Dinero valorTotal,
                           Dinero anticipo, LocalDateTime limiteConfirmacion, Dinero retencion) {

    public AvisoReserva {
        if (tipo == null || reserva == null || estancia == null || valorTotal == null) {
            throw new ReglaDominioException("El aviso de reserva requiere tipo, reserva, estancia y valor.");
        }
        if ((correo == null || correo.isBlank()) && (telefono == null || telefono.isBlank())) {
            throw new ReglaDominioException("El aviso necesita al menos un correo o un teléfono del titular.");
        }
    }
}
