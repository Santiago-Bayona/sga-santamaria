package co.edu.uniquindio.sga.application.dto.response;

import co.edu.uniquindio.sga.domain.valueobject.EstadoOperativo;

import java.util.List;

/** DTO de salida de la API. Detalle de un apartamento (7.1): dotación, galería y estado. */
public record ApartamentoResponse(

        String identificacion,
        String nombre,
        String descripcion,
        int dormitorios,
        int capacidad,
        List<String> dotacion,
        List<String> imagenes,
        String imagenPrincipal,
        EstadoOperativo estadoOperativo,
        boolean activo
) {
}
