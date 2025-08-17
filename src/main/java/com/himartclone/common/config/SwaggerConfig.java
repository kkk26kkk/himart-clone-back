package com.himartclone.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Himart Clone")
                        .description("Himart Clone")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 서버")
//                        new Server().url("https://api.example.com").description("운영 서버")
                ));
    }
}
