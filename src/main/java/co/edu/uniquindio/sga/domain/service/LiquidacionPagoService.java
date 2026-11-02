package co.edu.uniquindio.sga.domain.service;

import co.edu.uniquindio.sga.domain.entity.Folio;
import co.edu.uniquindio.sga.domain.entity.Reserva;
import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.repository.AlojamientoRepository;
import co.edu.uniquindio.sga.domain.repository.FolioRepository;
import co.edu.uniquindio.sga.domain.repository.ReservaRepository;
import co.edu.uniquindio.sga.domain.valueobject.*;

import java.time.LocalDate;

/**
 * Movimientos del folio (7.7). RN-15, RN-16 y RN-17 las garantiza el propio Folio; este servicio solo aporta
 * cuando se necesita otro agregado: el valor de un servicio adicional sale del Alojamiento y solo se carga a
 * reservas activas. Pagos, reversos y cierre son de un solo agregado y podrían ir directo en el caso de uso.
 */
public class LiquidacionPagoService {

    private final FolioRepository folioRepository;
    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;

    public LiquidacionPagoService(FolioRepository folioRepository, ReservaRepository reservaRepository,
                                  AlojamientoRepository alojamientoRepository) {
        this.folioRepository = folioRepository;
        this.reservaRepository = reservaRepository;
        this.alojamientoRepository = alojamientoRepository;
    }

    /** RN-15: todo pago se registra contra el folio con su medio y su fecha. */
    public ResultadoMovimientoFolio<Pago> registrarPago(CodigoReserva codigo, Dinero monto, MedioPago medio,
                                                        LocalDate fecha) {
        Folio folio = folio(codigo);
        Pago pago = folio.registrarPago(monto, medio, fecha);
        folioRepository.guardar(folio);
        return new ResultadoMovimientoFolio<>(codigo, pago, folio.calcularSaldo());
    }

    /** RN-16: un pago no se edita ni se borra; se corrige con un reverso que apunta a su numero. */
    public ResultadoMovimientoFolio<Pago> revertirPago(CodigoReserva codigo, int numeroPago, LocalDate fecha) {
        Folio folio = folio(codigo);
        Pago reverso = folio.revertirPago(numeroPago, fecha);
        folioRepository.guardar(folio);
        return new ResultadoMovimientoFolio<>(codigo, reverso, folio.calcularSaldo());
    }

    /** RN-16: un cargo no se edita ni se borra; se corrige con un reverso que apunta a su numero. */
    public ResultadoMovimientoFolio<Cargo> revertirCargo(CodigoReserva codigo, int numeroCargo, LocalDate fecha) {
        Folio folio = folio(codigo);
        Cargo reverso = folio.revertirCargo(numeroCargo, fecha);
        folioRepository.guardar(folio);
        return new ResultadoMovimientoFolio<>(codigo, reverso, folio.calcularSaldo());
    }

    /** 7.7: cargo por servicio adicional, con el valor configurado en el alojamiento (7.3). */
    public ResultadoMovimientoFolio<Cargo> registrarServicioAdicional(CodigoReserva codigo, String nombreServicio, LocalDate fecha) {
        Reserva reserva = reservaRepository.obtenerPorCodigo(codigo)
                .orElseThrow(() -> new ReglaDominioException("No existe la reserva " + codigo.valor()));
        if (!reserva.estaActiva()) {
            throw new ReglaDominioException("Solo se cargan servicios adicionales a reservas activas.");
        }
        ServicioAdicional servicio = alojamientoRepository.obtener().getServiciosAdicionales().stream()
                .filter(s -> s.nombre().equalsIgnoreCase(nombreServicio == null ? "" : nombreServicio.trim()))
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException("El alojamiento no ofrece el servicio " + nombreServicio));
        if (!servicio.generaCargo()) {
            throw new ReglaDominioException("El servicio " + servicio.nombre() + " no genera cargo.");
        }
        Folio folio = folio(codigo);
        Cargo cargo = folio.agregarCargo(TipoCargo.SERVICIO_ADICIONAL, servicio.nombre(), servicio.costo(), fecha);
        folioRepository.guardar(folio);
        return new ResultadoMovimientoFolio<>(codigo, cargo, folio.calcularSaldo());
    }

    /** RN-17: con saldo distinto de cero exige autorización explícita (autor y motivo). */
    public void cerrarFolio(CodigoReserva codigo, AutorizacionCierre autorizacion) {
        Folio folio = folio(codigo);
        if (folio.calcularSaldo().esCero() || autorizacion == null) {
            folio.cerrar();
        } else {
            folio.cerrarConAutorizacion(autorizacion);
        }
        folioRepository.guardar(folio);
    }

    private Folio folio(CodigoReserva codigo) {
        return folioRepository.obtenerPorReserva(codigo)
                .orElseThrow(() -> new ReglaDominioException("La reserva " + codigo.valor() + " no tiene folio."));
    }
}
