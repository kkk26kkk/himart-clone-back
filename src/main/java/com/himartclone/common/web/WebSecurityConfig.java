package com.himartclone.common.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//        http.cors(cors -> cors.configurationSource(CorsConfig.corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    static class CorsConfig {

        public static CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();

            //리소스를 허용할 URL 지정
            ArrayList<String> allowedOriginPatterns = new ArrayList<>();
            allowedOriginPatterns.add("http://localhost:3000");
            allowedOriginPatterns.add("http://127.0.0.1:3000");
            allowedOriginPatterns.add("http://localhost:8080");
            allowedOriginPatterns.add("http://127.0.0.1:8080");
            configuration.setAllowedOrigins(allowedOriginPatterns);

            //허용하는 HTTP METHOD 지정
            ArrayList<String> allowedHttpMethods = new ArrayList<>();
            allowedHttpMethods.add("GET");
            allowedHttpMethods.add("POST");
            allowedHttpMethods.add("PUT");
            allowedHttpMethods.add("PATCH");
            allowedHttpMethods.add("DELETE");
            configuration.setAllowedMethods(allowedHttpMethods);

            configuration.setAllowedHeaders(Collections.singletonList("*"));
//            configuration.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));

            //인증, 인가를 위한 credentials 를 TRUE로 설정
            configuration.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);

            return source;
        }
    }
}
