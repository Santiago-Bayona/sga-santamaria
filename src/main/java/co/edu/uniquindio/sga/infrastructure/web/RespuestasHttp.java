package co.edu.uniquindio.sga.infrastructure.web;

import org.springframework.http.ResponseEntity;

import java.net.URI;

/**
 * Arma la respuesta 201 con el cuerpo y la cabecera Location apuntando al recurso creado.
 */

public final class RespuestasHttp {

    private RespuestasHttp() {
    }

    public static <T> ResponseEntity<T> creado(String ruta, T cuerpo) {
        return ResponseEntity.created(URI.create(ruta)).body(cuerpo);
    }
}
