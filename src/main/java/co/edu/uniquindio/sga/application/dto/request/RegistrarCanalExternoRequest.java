package co.edu.uniquindio.sga.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Datos para registrar un canal de venta externo (7.8). La credencial es la que usará el canal para autenticarse. */

public record RegistrarCanalExternoRequest(

        @NotBlank(message = "El código del canal es obligatorio")
        String codigoCanal,

        @NotBlank(message = "El tipo del canal es obligatorio")
        String tipo,

        @NotBlank(message = "La credencial del canal es obligatoria")
        String credencial

) {
}
