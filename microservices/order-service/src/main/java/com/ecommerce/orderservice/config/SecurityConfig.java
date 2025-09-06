package com.ecommerce.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuração de segurança para o Order Service
 * 
 * Define as regras de acesso aos endpoints:
 * - Endpoints públicos: health check
 * - Endpoints protegidos: operações de pedidos (requerem autenticação)
 * - CORS habilitado para desenvolvimento
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF para APIs REST
                .csrf(csrf -> csrf.disable())
                
                // Configuração CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Configuração de sessão stateless
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Configuração de autorização
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/health").permitAll()
                        
                        // Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        
                        // Actuator endpoints (se habilitados)
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // Eureka endpoints (para registro no service discovery)
                        .requestMatchers("/eureka/**").permitAll()
                        
                        // Todos os outros endpoints requerem autenticação
                        .anyRequest().authenticated()
                );
                
        return http.build();
    }

    /**
     * Configuração CORS para permitir acesso de diferentes origens
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permite todas as origens em desenvolvimento
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Permite todos os métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Permite todos os headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permite credentials
        configuration.setAllowCredentials(true);
        
        // Expõe headers de resposta
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
