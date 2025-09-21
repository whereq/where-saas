package com.whereq.spark.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sparkServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spark as a Service API")
                        .description("RESTful API for submitting and managing Apache Spark jobs")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("WhereQ")
                                .email("support@whereq.com")
                                .url("https://github.com/whereq"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production server")
                ));
    }
}