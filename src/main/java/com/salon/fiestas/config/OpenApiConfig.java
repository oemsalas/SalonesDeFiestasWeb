package com.salon.fiestas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Salón de Fiestas API")
                .version("1.0.0")
                .description("API REST para gestión integral de reservas, disponibilidad, servicios y pagos de un salón de fiestas.")
                .contact(new Contact().name("Administración").email("admin@salon.com")))
            .servers(List.of(
                new Server().url("http://localhost:8080/api").description("Desarrollo local")
            ));
    }
}
