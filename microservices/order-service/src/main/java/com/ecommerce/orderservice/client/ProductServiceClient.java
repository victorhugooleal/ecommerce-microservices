package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.client.dto.ProductResponseDTO;
import com.ecommerce.orderservice.client.dto.UpdateStockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign Client para comunicação com o Product Service
 */
@FeignClient(
        name = "product-service",
        url = "${services.product-service.url:http://localhost:8082}",
        path = "/api/products"
)
public interface ProductServiceClient {

    /**
     * Buscar produto por ID
     */
    @GetMapping("/{id}")
    ResponseEntity<ProductResponseDTO> getProductById(@PathVariable("id") Long id);

    /**
     * Verificar disponibilidade de estoque
     */
    @GetMapping("/{id}/stock-check")
    ResponseEntity<Map<String, Object>> checkStockAvailability(
            @PathVariable("id") Long id,
            @RequestParam("quantity") Integer quantity
    );

    /**
     * Atualizar estoque do produto
     */
    @PatchMapping("/stock")
    ResponseEntity<ProductResponseDTO> updateStock(@RequestBody UpdateStockDTO updateStockDTO);
}
