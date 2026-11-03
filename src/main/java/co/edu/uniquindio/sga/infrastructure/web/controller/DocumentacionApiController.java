package co.edu.uniquindio.sga.infrastructure.web.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;
/**
 * GET /api/docs. Texto corto que indica dónde está Swagger. La especificación viva la genera springdoc.
 */
@Hidden
@RestController
public class DocumentacionApiController {

    @GetMapping(value = "/api/docs", produces = MediaType.TEXT_PLAIN_VALUE)
    public String indice() {
        return """
                SGA Santamaría — API
                Swagger UI: GET /swagger-ui.html
                OpenAPI: GET /v3/api-docs
                Copia de referencia: docs/api/openapi.yaml
                Auth: POST /api/auth/login  { "username", "password" }
                Health: GET /actuator/health
                H2 (dev): /h2-console
                """;
    }
}
