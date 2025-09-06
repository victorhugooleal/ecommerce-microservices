package com.ecommerce.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server - Service Discovery & Registry
 * 
 * Responsável por:
 * - Descoberta automática de serviços (Service Discovery)
 * - Registro centralizado de todos os microsserviços
 * - Monitoramento de saúde dos serviços
 * - Load balancing entre instâncias
 * - Failover automático
 * 
 * Porta: 8761 (padrão do Netflix Eureka)
 * Web UI: http://localhost:8761
 * 
 * Endpoints importantes:
 * - /eureka/apps - Lista todos os serviços registrados
 * - /eureka/apps/{service-name} - Detalhes de um serviço específico
 * - /actuator/health - Health check do próprio Eureka
 * 
 * Microsserviços registrados:
 * - user-service (8081)
 * - product-service (8082)
 * - order-service (8083)
 * - api-gateway (8080)
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
