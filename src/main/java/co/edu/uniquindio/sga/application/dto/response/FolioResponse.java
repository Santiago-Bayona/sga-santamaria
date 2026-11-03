package co.edu.uniquindio.sga.application.dto.response;

import java.util.List;

/** DTO de salida de la API. Folio con su detalle completo (7.7). saldo: positivo = debe el huésped, cero, o negativo = a favor. */
public record FolioResponse(

        String codigo,
        String codigoReserva,
        List<CargoResponse> cargos,
        List<PagoResponse> pagos,
        long totalCargos,
        long totalPagos,
        long saldo,
        String moneda,
        boolean cerrado,
        String autorCierre,     // solo si se cerró con saldo pendiente (RN-17)
        String motivoCierre
) {
}
