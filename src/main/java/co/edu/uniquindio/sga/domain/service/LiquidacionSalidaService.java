package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Apartamento;
import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.ApartamentoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.AutorizacionCierre;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.EstadoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Salida;

import java.time.LocalDateTime;

/**
 * 7.6 registro de salida: el cierre del folio es requisito, salvo autorización del administrador (RN-17);
 * la reserva pasa a FINALIZADA y el apartamento a PENDIENTE_PREPARACION. Coordina tres agregados con
 * fronteras de consistencia propias. Una salida anticipada se maneja igual: la política no aplica.
 */
public class LiquidacionSalidaService {

    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final GestionRecordatoriosService recordatorios;

    public LiquidacionSalidaService(ReservaRepository reservaRepository, FolioRepository folioRepository,
                                    ApartamentoRepository apartamentoRepository,
                                    GestionRecordatoriosService recordatorios) {
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.apartamentoRepository = apartamentoRepository;
        this.recordatorios = recordatorios;
    }

    /** @param autorizacion obligatoria solo si el saldo del folio es distinto de cero; si no, puede ser null */
    public void registrarSalida(CodigoReserva codigo, LocalDateTime ahora, AutorizacionCierre autorizacion) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReglaDominioException("No existe la reserva " + codigo.valor()));
        if (reserva.getEstado() != EstadoReserva.EN_CURSO) {
            throw new ReglaDominioException("Solo puede registrarse la salida de una reserva EN_CURSO.");
        }
        Folio folio = folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new ReglaDominioException("La reserva " + codigo.valor() + " no tiene folio."));
        Apartamento apartamento = apartamentoRepository.obtenerPorIdentificacion(reserva.getApartamento())
                .orElseThrow(() -> new ReglaDominioException(
                        "No existe el apartamento " + reserva.getApartamento().valor()));

        if (!folio.estaCerrado()) {
            // RN-17: con saldo cero se cierra normal; con saldo pendiente solo con autorización registrada
            if (folio.calcularSaldo().esCero() || autorizacion == null) {
                folio.cerrar();
            } else {
                folio.cerrarConAutorizacion(autorizacion);
            }
        }
        reserva.registrarSalida(new Salida(ahora));
        apartamento.marcarPendientePreparacion();

        folioRepository.guardar(folio);
        reservaRepository.guardar(reserva);
        apartamentoRepository.guardar(apartamento);
        recordatorios.cancelarPendientes(codigo); // salida anticipada: el recordatorio de salida ya no aplica
    }
}
