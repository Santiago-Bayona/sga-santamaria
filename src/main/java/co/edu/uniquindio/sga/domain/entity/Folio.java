package co.edu.uniquindio.sga.domain.entity;

import co.edu.uniquindio.sga.domain.exception.ReglaDominioException;
import co.edu.uniquindio.sga.domain.valueobject.AutorizacionCierre;
import co.edu.uniquindio.sga.domain.valueobject.Cargo;
import co.edu.uniquindio.sga.domain.valueobject.CodigoFolio;
import co.edu.uniquindio.sga.domain.valueobject.CodigoReserva;
import co.edu.uniquindio.sga.domain.valueobject.Dinero;
import co.edu.uniquindio.sga.domain.valueobject.MedioPago;
import co.edu.uniquindio.sga.domain.valueobject.Pago;
import co.edu.uniquindio.sga.domain.valueobject.Saldo;
import co.edu.uniquindio.sga.domain.valueobject.TipoCargo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Raíz del agregado Folio: cuenta de una sola reserva (7.7). Cargos y pagos son append-only (RN-16) y tienen
 * identidad local: el Folio les asigna un numero consecutivo y toda corrección es un reverso que apunta a ese
 * numero. Cada movimiento se revierte a lo sumo una vez.
 */
public class Folio {

    private final CodigoFolio codigo;
    private final CodigoReserva reserva;
    private List<Cargo> cargos;
    private List<Pago> pagos = List.of();
    private boolean cerrado;
    private AutorizacionCierre autorizacionCierre;

    private Folio(CodigoFolio codigo, CodigoReserva reserva, Cargo cargoAlojamiento) {
        this.codigo = codigo;
        this.reserva = reserva;
        this.cargos = List.of(cargoAlojamiento);
    }

    /**
     * Reconstruye un folio guardado. Exige la numeración consecutiva que garantiza RN-16
     * (nada se borró) y que la autorización exista solo si está cerrado.
     */
    public static Folio rehidratar(CodigoFolio codigo, CodigoReserva reserva, List<Cargo> cargos, List<Pago> pagos,
                                   boolean cerrado, AutorizacionCierre autorizacionCierre) {
        if (codigo == null || reserva == null || cargos == null || cargos.isEmpty() || pagos == null) {
            throw new ReglaDominioException("El folio guardado está incompleto.");
        }
        for (int i = 0; i < cargos.size(); i++) {
            if (cargos.get(i).numero() != i + 1) {
                throw new ReglaDominioException("Los cargos del folio guardado no tienen numeración consecutiva.");
            }
        }
        for (int i = 0; i < pagos.size(); i++) {
            if (pagos.get(i).numero() != i + 1) {
                throw new ReglaDominioException("Los pagos del folio guardado no tienen numeración consecutiva.");
            }
        }
        if (autorizacionCierre != null && !cerrado) {
            throw new ReglaDominioException("Un folio abierto no puede tener autorización de cierre.");
        }
        Folio folio = new Folio(codigo, reserva, cargos.getFirst());
        folio.cargos = List.copyOf(cargos);
        folio.pagos = List.copyOf(pagos);
        folio.cerrado = cerrado;
        folio.autorizacionCierre = autorizacionCierre;
        return folio;
    }

    /** 7.7: el folio se abre al crear la reserva, con el cargo de alojamiento calculado. */
    public static Folio abrir(CodigoFolio codigo, CodigoReserva reserva, String concepto, Dinero valorAlojamiento,
                              LocalDate fecha) {
        if (codigo == null || reserva == null) {
            throw new ReglaDominioException("El folio requiere código y reserva.");
        }
        return new Folio(codigo, reserva, Cargo.suma(1, TipoCargo.ALOJAMIENTO, concepto, valorAlojamiento, fecha));
    }

    // ---- Movimientos ----

    public Cargo agregarCargo(TipoCargo tipo, String concepto, Dinero monto, LocalDate fecha) {
        exigirAbierto();
        if (tipo == TipoCargo.AJUSTE) {
            throw new ReglaDominioException("Los ajustes se registran con registrarAjuste.");
        }
        return agregar(Cargo.suma(siguienteCargo(), tipo, concepto, monto, fecha));
    }

    /** RN-15: todo pago se registra contra el folio con su medio y su fecha. */
    public Pago registrarPago(Dinero monto, MedioPago medio, LocalDate fecha) {
        exigirAbierto();
        Pago pago = Pago.original(siguientePago(), monto, medio, fecha);
        pagos = conAgregado(pagos, pago);
        return pago;
    }

    /** RN-16: corrige un cargo sin editarlo ni borrarlo, identificándolo por su numero. */
    public Cargo revertirCargo(int numero, LocalDate fecha) {
        exigirAbierto();
        Cargo original = cargos.stream().filter(c -> c.numero() == numero).findFirst()
                .orElseThrow(() -> new ReglaDominioException("El folio no tiene un cargo #" + numero + "."));
        if (cargoRevertido(numero)) {
            throw new ReglaDominioException("El cargo #" + numero + " ya fue revertido.");
        }
        Cargo reverso = Cargo.reverso(siguienteCargo(), original, fecha);
        // Revertir un cargo que suma cuando ya hay descuentos podría dejar el total en negativo
        if (totalCargos().valor().add(reverso.valorConSigno()).signum() < 0) {
            throw new ReglaDominioException("Revertir el cargo #" + numero + " dejaría los cargos del folio en negativo.");
        }
        return agregar(reverso);
    }

    /** RN-16: corrige un pago sin editarlo ni borrarlo, identificándolo por su numero. */
    public Pago revertirPago(int numero, LocalDate fecha) {
        exigirAbierto();
        Pago original = pagos.stream().filter(p -> p.numero() == numero).findFirst()
                .orElseThrow(() -> new ReglaDominioException("El folio no tiene un pago #" + numero + "."));
        if (pagoRevertido(numero)) {
            throw new ReglaDominioException("El pago #" + numero + " ya fue revertido.");
        }
        Pago reverso = Pago.reverso(siguientePago(), original, fecha);
        pagos = conAgregado(pagos, reverso);
        return reverso;
    }

    /** 3.5 / RN-14: la diferencia (positiva o negativa) de una modificación se registra como ajuste. */
    public Cargo registrarAjuste(BigDecimal diferencia, LocalDate fecha) {
        exigirAbierto();
        if (diferencia == null || diferencia.signum() == 0) {
            throw new ReglaDominioException("No hay diferencia que registrar como ajuste.");
        }
        String concepto = "Ajuste por modificación de la reserva";
        Dinero monto = new Dinero(diferencia.abs());
        return agregar(diferencia.signum() > 0
                ? Cargo.suma(siguienteCargo(), TipoCargo.AJUSTE, concepto, monto, fecha)
                : Cargo.descuento(siguienteCargo(), TipoCargo.AJUSTE, concepto, monto, fecha));
    }

    /**
     * 7.5: al cancelar (o declarar no-show) se revierte cada cargo vigente, uno por uno y con trazabilidad,
     * y se registra la retención como penalidad. Si lo pagado supera la retención, el saldo queda a favor:
     * la devolución se calcula y se deja registrada, el desembolso ocurre fuera del sistema (3.4).
     */
    public void liquidarRetencion(Dinero retencion, LocalDate fecha) {
        exigirAbierto();
        if (retencion == null || fecha == null) {
            throw new ReglaDominioException("La retención y la fecha son obligatorias.");
        }
        List<Cargo> vigentes = cargos.stream()
                .filter(c -> !c.esReverso() && !cargoRevertido(c.numero()))
                .toList();
        for (Cargo cargo : vigentes) {
            agregar(Cargo.reverso(siguienteCargo(), cargo, fecha));
        }
        if (!retencion.esCero()) {
            agregar(Cargo.suma(siguienteCargo(), TipoCargo.PENALIDAD,
                    "Retención según política de cancelación", retencion, fecha));
        }
    }

    // ---- Consultas ----

    public boolean cargoRevertido(int numero) {
        return cargos.stream().anyMatch(c -> c.esReverso() && c.revierteA() == numero);
    }

    public boolean pagoRevertido(int numero) {
        return pagos.stream().anyMatch(p -> p.esReverso() && p.revierteA() == numero);
    }

    /** Suma neta de cargos. Puede quedar en cero, nunca negativa: los descuentos no superan lo cargado. */
    public Dinero totalCargos() {
        return new Dinero(cargos.stream().map(Cargo::valorConSigno).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public Dinero totalPagos() {
        return new Dinero(pagos.stream().map(Pago::valorConSigno).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /** El saldo es la diferencia entre cargos y pagos; puede ser positivo, cero o a favor. */
    public Saldo calcularSaldo() {
        return Saldo.entre(totalCargos(), totalPagos());
    }

    /** RN-17: sin autorización explícita, solo se cierra con saldo cero. */
    public void cerrar() {
        exigirAbierto();
        if (!calcularSaldo().esCero()) {
            throw new ReglaDominioException("El folio no puede cerrarse con saldo distinto de cero sin autorización.");
        }
        this.cerrado = true;
    }

    public void cerrarConAutorizacion(AutorizacionCierre autorizacion) {
        exigirAbierto();
        if (autorizacion == null) {
            throw new ReglaDominioException("La autorización de cierre es obligatoria.");
        }
        this.autorizacionCierre = autorizacion;
        this.cerrado = true;
    }

    // ---- Internos ----

    private Cargo agregar(Cargo cargo) {
        cargos = conAgregado(cargos, cargo);
        return cargo;
    }

    /** Append-only: los números son consecutivos porque nunca se borra nada. */
    private int siguienteCargo() {
        return cargos.size() + 1;
    }

    private int siguientePago() {
        return pagos.size() + 1;
    }

    private static <T> List<T> conAgregado(List<T> lista, T elemento) {
        List<T> actualizada = new ArrayList<>(lista);
        actualizada.add(elemento);
        return List.copyOf(actualizada);
    }

    private void exigirAbierto() {
        if (cerrado) {
            throw new ReglaDominioException("El folio está cerrado y no admite más movimientos.");
        }
    }

    // ---- Getters ----

    public CodigoFolio getCodigo() {
        return codigo;
    }

    public CodigoReserva getReserva() {
        return reserva;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public boolean estaCerrado() {
        return cerrado;
    }

    public AutorizacionCierre getAutorizacionCierre() {
        return autorizacionCierre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Folio that)) return false;
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
