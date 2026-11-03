package co.edu.uniquindio.sga.application.dto.response;

/** DTO de salida de la API. Un tramo de la política: desde X días de antelación se retiene Y%. */

public record TramoCancelacionResponse(
        int diasAntelacionMinima,
        int porcentajeRetencion
) {
}
