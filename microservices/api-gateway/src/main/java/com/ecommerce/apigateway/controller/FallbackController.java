package com.ecommerce.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller para endpoints de fallback
 * Acionado quando os circuit breakers estão abertos
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Fallback para User Service
     */
    @GetMapping("/user-service")
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "user-service",
                        "status", "UNAVAILABLE",
                        "message", "O serviço de usuários está temporariamente indisponível. Tente novamente em alguns momentos.",
                        "timestamp", LocalDateTime.now(),
                        "fallback", true
                ));
    }

    /**
     * Fallback para Product Service
     */
    @GetMapping("/product-service")
    public ResponseEntity<Map<String, Object>> productServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "product-service",
                        "status", "UNAVAILABLE", 
                        "message", "O serviço de produtos está temporariamente indisponível. Tente novamente em alguns momentos.",
                        "timestamp", LocalDateTime.now(),
                        "fallback", true
                ));
    }

    /**
     * Fallback para Order Service
     */
    @GetMapping("/order-service")
    public ResponseEntity<Map<String, Object>> orderServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "order-service",
                        "status", "UNAVAILABLE",
                        "message", "O serviço de pedidos está temporariamente indisponível. Tente novamente em alguns momentos.",
                        "timestamp", LocalDateTime.now(),
                        "fallback", true
                ));
    }

    /**
     * Fallback genérico
     */
    @GetMapping("/generic")
    public ResponseEntity<Map<String, Object>> genericFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "SERVICE_UNAVAILABLE",
                        "message", "Serviço temporariamente indisponível. Nossa equipe foi notificada e está trabalhando na resolução.",
                        "timestamp", LocalDateTime.now(),
                        "fallback", true,
                        "support", "Entre em contato com nosso suporte se o problema persistir"
                ));
    }
}
