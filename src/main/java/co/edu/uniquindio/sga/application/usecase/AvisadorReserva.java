package co.edu.uniquindio.sga.application.usecase;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.port.NotificacionReservaPort;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.TitularRepository;
import co.edu.uniquindio.sga.domain.valueobject.AvisoReserva;
import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.TipoAvisoReserva;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Arma el aviso al titular (7.5, 7.8) con los datos de la reserva y lo entrega al puerto de notificación.
 * Lo usan los casos de uso de crear, confirmar y cancelar. Nunca hace fallar la operación que lo llama.
 */
@Component
public class AvisadorReserva {

    private static final Logger LOG = LoggerFactory.getLogger(AvisadorReserva.class);

    private final NotificacionReservaPort notificacion;
    private final TitularRepository titulares;
    private final ApartamentoRepository apartamentos;
    private final AlojamientoRepository alojamiento;

    public AvisadorReserva(NotificacionReservaPort notificacion, TitularRepository titulares,
                           ApartamentoRepository apartamentos, AlojamientoRepository alojamiento) {
        this.notificacion = notificacion;
        this.titulares = titulares;
        this.apartamentos = apartamentos;
        this.alojamiento = alojamiento;
    }

    public void creada(Reserva reserva) {
        ConfiguracionAlojamiento configuracion = alojamiento.obtener().getConfiguracion();
        Dinero anticipo = configuracion.exigeAnticipo() ? configuracion.anticipoSobre(reserva.getValorCongelado()) : null;
        LocalDateTime limite = reserva.getFechaCreacion().plusHours(configuracion.plazoConfirmacion().horas());
        avisar(TipoAvisoReserva.CREADA, reserva, anticipo, limite, null);
    }

    public void confirmada(Reserva reserva) {
        avisar(TipoAvisoReserva.CONFIRMADA, reserva, null, null, null);
    }

    public void cancelada(Reserva reserva, Dinero retencion) {
        avisar(TipoAvisoReserva.CANCELADA, reserva, null, null, retencion);
    }

    private void avisar(TipoAvisoReserva tipo, Reserva reserva, Dinero anticipo, LocalDateTime limite, Dinero retencion) {
        try {
            titulares.obtenerPorCodigo(reserva.getTitular()).ifPresentOrElse(titular -> {
                String apartamento = apartamentos.obtenerPorIdentificacion(reserva.getApartamento())
                        .map(Apartamento::getNombre)
                        .orElse(reserva.getApartamento().valor());
                notificacion.notificar(new AvisoReserva(tipo, reserva.getCodigo(), titular.getNombre(),
                        titular.getCorreo(), titular.getTelefono(), apartamento, reserva.getEstancia(),
                        reserva.getValorCongelado(), anticipo, limite, retencion));
            }, () -> LOG.warn("Reserva {} sin titular registrado: no se envía el aviso {}", reserva.getCodigo().valor(), tipo));
        } catch (RuntimeException e) {
            // El aviso es un efecto secundario: si algo falla al armarlo, la reserva no se deshace por eso.
            LOG.error("No se pudo preparar el aviso {} de la reserva {}: {}", tipo, reserva.getCodigo().valor(), e.getMessage());
        }
    }
}
