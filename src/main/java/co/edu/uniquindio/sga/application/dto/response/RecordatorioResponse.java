package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.EstadoRecordatorio;
import co.edu.uniquindio.sga.domain.valueobject.TipoRecordatorio;

import java.time.LocalDateTime;

/** DTO de salida de la API. Un recordatorio programado de una reserva (RN-23). */
public record RecordatorioResponse(

        String codigo,
        String codigoReserva,
        TipoRecordatorio tipo,
        LocalDateTime momentoEvento,
        LocalDateTime fechaRecordatorio,
        EstadoRecordatorio estado,
        String ultimoError
) {
}
