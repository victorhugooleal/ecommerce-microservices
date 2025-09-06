package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.client.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client para comunicação com o User Service
 */
@FeignClient(
        name = "user-service",
        url = "${services.user-service.url:http://localhost:8081}",
        path = "/api/users"
)
public interface UserServiceClient {

    /**
     * Buscar usuário por ID
     */
    @GetMapping("/{id}")
    ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") Long id);

    /**
     * Verificar se usuário existe
     */
    @GetMapping("/{id}")
    ResponseEntity<UserResponseDTO> checkUserExists(@PathVariable("id") Long id);
}
