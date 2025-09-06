package com.ecommerce.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway - Ponto Único de Entrada
 * 
 * Responsável por:
 * - Roteamento inteligente para microsserviços
 * - Load balancing automático
 * - Circuit breaker para resiliência
 * - CORS configuration
 * - Autenticação e autorização centralizadas
 * - Rate limiting e throttling
 * - Logging e monitoramento de requests
 * 
 * Porta: 8080
 * 
 * Rotas disponíveis:
 * - /api/v1/users/** → user-service (8081)
 * - /api/v1/auth/** → user-service (8081)
 * - /api/v1/products/** → product-service (8082)
 * - /api/v1/orders/** → order-service (8083)
 * - /eureka/web → eureka-server web UI (8761)
 * 
 * Features:
 * - Service Discovery via Eureka
 * - Circuit Breaker com Resilience4j
 * - Fallback endpoints para alta disponibilidade
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
