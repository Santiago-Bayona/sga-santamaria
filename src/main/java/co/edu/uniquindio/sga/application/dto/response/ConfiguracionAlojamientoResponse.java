package co.edu.uniquindio.sga.application.dto.response;

import java.time.LocalTime;

/** DTO de salida de la API. Parámetros configurables del alojamiento (sección 6). Duraciones en horas. */

public record ConfiguracionAlojamientoResponse(
        LocalTime horaEntrada,
        LocalTime horaSalida,
        long tiempoPreparacionMinutos,
        int plazoConfirmacionHoras,
        LocalTime horaLimiteNoShow,
        int umbralEdadFacturable,
        int porcentajeAnticipo,
        long antelacionRecordatorioEntradaHoras,
        long antelacionRecordatorioSalidaHoras
) {
}
