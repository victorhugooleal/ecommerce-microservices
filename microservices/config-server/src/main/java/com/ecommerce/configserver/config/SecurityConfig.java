package com.ecommerce.configserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança para o Config Server
 * 
 * Define as regras de acesso:
 * - Endpoints de actuator são públicos
 * - Endpoints de configuração requerem autenticação básica
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // Todos os outros endpoints requerem autenticação
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {
                // Configuração de autenticação básica já está no application.yml
            });
            
        return http.build();
    }
}
