package com.example.studentmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Customizes the metadata shown at the top of the generated Swagger UI
 * (title, description, version, contact info). Springdoc auto-generates
 * the actual endpoint documentation by scanning our @RestController
 * classes and @Operation annotations — this bean just adds the
 * human-friendly header info.
 *
 * Swagger UI will be available at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentManagementOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Management System API")
                        .description("Production-style REST API for managing student records")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Engineering Team")
                                .email("engineering@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
}
