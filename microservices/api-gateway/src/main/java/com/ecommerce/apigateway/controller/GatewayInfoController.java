package com.ecommerce.apigateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para informações e monitoramento do Gateway
 */
@RestController
@RequestMapping("/gateway")
public class GatewayInfoController {

    @Autowired
    private RouteLocator routeLocator;

    /**
     * Health check do Gateway
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "api-gateway");
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Informações do Gateway
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "E-commerce API Gateway");
        response.put("description", "Single entry point for all E-commerce microservices");
        response.put("version", "1.0.0");
        response.put("port", 8080);
        response.put("timestamp", LocalDateTime.now());
        
        // Informações das rotas
        Map<String, String> routes = new HashMap<>();
        routes.put("/api/v1/users/**", "user-service");
        routes.put("/api/v1/auth/**", "user-service"); 
        routes.put("/api/v1/products/**", "product-service");
        routes.put("/api/v1/orders/**", "order-service");
        routes.put("/eureka/web", "eureka-server");
        
        response.put("routes", routes);
        
        // Features disponíveis
        Map<String, Boolean> features = new HashMap<>();
        features.put("service_discovery", true);
        features.put("load_balancing", true);
        features.put("circuit_breaker", true);
        features.put("cors_support", true);
        features.put("fallback_endpoints", true);
        
        response.put("features", features);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Status dos circuit breakers
     */
    @GetMapping("/circuit-breakers")
    public ResponseEntity<Map<String, Object>> circuitBreakers() {
        Map<String, Object> response = new HashMap<>();
        
        // Status dos circuit breakers (simplificado)
        Map<String, String> circuitBreakers = new HashMap<>();
        circuitBreakers.put("user-service-cb", "CLOSED");
        circuitBreakers.put("product-service-cb", "CLOSED");
        circuitBreakers.put("order-service-cb", "CLOSED");
        
        response.put("circuit_breakers", circuitBreakers);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de boas-vindas
     */
    @GetMapping("/welcome")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bem-vindo ao E-commerce API Gateway!");
        response.put("description", "Este é o ponto único de entrada para todos os microsserviços");
        response.put("available_services", Map.of(
                "user-service", "http://localhost:8080/api/v1/users",
                "product-service", "http://localhost:8080/api/v1/products", 
                "order-service", "http://localhost:8080/api/v1/orders",
                "eureka-ui", "http://localhost:8080/eureka/web"
        ));
        response.put("documentation", "Consulte a documentação de cada serviço para detalhes das APIs");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}
