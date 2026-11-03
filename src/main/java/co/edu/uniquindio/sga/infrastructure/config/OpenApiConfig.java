package co.edu.uniquindio.sga.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publica la especificación OpenAPI con el esquema Bearer JWT. Swagger UI la consume en /swagger-ui.html.
 */

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sgaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SGA Santamaría")
                        .version("1.0.0")
                        .description("API del sistema de gestión de alojamiento. Autenticación JWT Bearer."))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
