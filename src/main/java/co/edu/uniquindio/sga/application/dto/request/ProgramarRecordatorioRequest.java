package co.edu.uniquindio.sga.application.dto.request;

import co.edu.uniquindio.sga.domain.valueobject.TipoRecordatorio;
import jakarta.validation.constraints.NotNull;

public record ProgramarRecordatorioRequest(

        @NotNull(message = "El tipo de recordatorio es obligatorio")
        TipoRecordatorio tipo

) {
}