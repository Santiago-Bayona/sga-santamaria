package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;

import java.time.LocalDateTime;

/**
 * Sección 8: PENDIENTE → CONFIRMADA, con anticipo si el alojamiento lo exige (L-11). El anticipo está en el
 * Folio y su monto en la configuración del Alojamiento; la transición es de la Reserva. Al confirmar se
 * programan los recordatorios (RN-23).
 */
public class ConfirmacionReservaService {

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final GestionRecordatoriosService recordatorios;

    public ConfirmacionReservaService(ReservaRepository reservaRepository, FolioRepository folioRepository,
                                      AlojamientoRepository alojamientoRepository,
                                      GestionRecordatoriosService recordatorios) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.alojamientoRepository = alojamientoRepository;
        this.recordatorios = recordatorios;
    }

    public Reserva confirmar(CodigoReserva codigo, LocalDateTime ahora) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReglaDominioException("No existe la reserva " + codigo.valor()));
        Folio folio = folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new ReglaDominioException("La reserva " + codigo.valor() + " no tiene folio."));
        ConfiguracionAlojamiento configuracion = alojamientoRepository.obtener().getConfiguracion();

        // RN-21: si ya venció el plazo, no se confirma aunque la tarea automática aún no la haya cancelado
        if (reserva.haSuperadoPlazoConfirmacion(ahora, configuracion.plazoConfirmacion())) {
            throw new ReglaDominioException("La reserva superó el plazo de confirmación y ya no puede confirmarse.");
        }
        if (configuracion.exigeAnticipo()) {
            Dinero anticipo = configuracion.anticipoSobre(reserva.getValorCongelado());
            if (anticipo.esMayorQue(folio.totalPagos())) {
                throw new ReglaDominioException("Para confirmar debe estar pagado el anticipo de " + anticipo.valor() + " COP.");
            }
        }
        reserva.confirmar(); // RN-08 y RN-09
        reservaRepository.guardar(reserva);
        recordatorios.programarRecordatoriosDeConfirmacion(reserva);
        return reserva;
    }
}
