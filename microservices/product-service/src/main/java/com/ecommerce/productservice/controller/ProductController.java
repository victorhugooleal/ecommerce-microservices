package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.CreateProductDTO;
import com.ecommerce.productservice.dto.ProductResponseDTO;
import com.ecommerce.productservice.dto.UpdateStockDTO;
import com.ecommerce.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de produtos
 * Endpoints para CRUD de produtos, controle de estoque e buscas
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Endpoints para gerenciamento de produtos e estoque")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    private final ProductService productService;

    // ============= ENDPOINTS PÚBLICOS (SEM AUTENTICAÇÃO) =============

    /**
     * Listar todos os produtos ativos
     */
    @GetMapping
    @Operation(summary = "Listar produtos ativos", 
               description = "Retorna lista de todos os produtos ativos")
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada")
    public ResponseEntity<List<ProductResponseDTO>> getAllActiveProducts() {
        log.info("Request para listar produtos ativos");
        List<ProductResponseDTO> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Buscar produto por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", 
               description = "Retorna produto pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<?> getProductById(
            @PathVariable @Parameter(description = "ID do produto") Long id) {
        
        try {
            log.info("Buscando produto por ID: {}", id);
            ProductResponseDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
            
        } catch (Exception e) {
            log.error("Erro ao buscar produto por ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Buscar produto por SKU
     */
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Buscar produto por SKU", 
               description = "Retorna produto pelo SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<?> getProductBySku(
            @PathVariable @Parameter(description = "SKU do produto") String sku) {
        
        try {
            log.info("Buscando produto por SKU: {}", sku);
            ProductResponseDTO product = productService.getProductBySku(sku);
            return ResponseEntity.ok(product);
            
        } catch (Exception e) {
            log.error("Erro ao buscar produto por SKU {}: {}", sku, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Listar produtos disponíveis (com estoque)
     */
    @GetMapping("/available")
    @Operation(summary = "Listar produtos disponíveis", 
               description = "Retorna produtos com estoque disponível")
    public ResponseEntity<List<ProductResponseDTO>> getAvailableProducts() {
        log.info("Listando produtos disponíveis");
        List<ProductResponseDTO> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Listar produtos em destaque
     */
    @GetMapping("/featured")
    @Operation(summary = "Listar produtos em destaque", 
               description = "Retorna produtos marcados como destaque")
    public ResponseEntity<List<ProductResponseDTO>> getFeaturedProducts() {
        log.info("Listando produtos em destaque");
        List<ProductResponseDTO> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Buscar produtos por categoria
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Buscar produtos por categoria", 
               description = "Retorna produtos de uma categoria específica")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(
            @PathVariable @Parameter(description = "Nome da categoria") String category) {
        
        log.info("Buscando produtos por categoria: {}", category);
        List<ProductResponseDTO> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Buscar produtos por marca
     */
    @GetMapping("/brand/{brand}")
    @Operation(summary = "Buscar produtos por marca", 
               description = "Retorna produtos de uma marca específica")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByBrand(
            @PathVariable @Parameter(description = "Nome da marca") String brand) {
        
        log.info("Buscando produtos por marca: {}", brand);
        List<ProductResponseDTO> products = productService.getProductsByBrand(brand);
        return ResponseEntity.ok(products);
    }

    /**
     * Buscar produtos por faixa de preço
     */
    @GetMapping("/price-range")
    @Operation(summary = "Buscar produtos por faixa de preço", 
               description = "Retorna produtos dentro de uma faixa de preço")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByPriceRange(
            @RequestParam @Parameter(description = "Preço mínimo") BigDecimal minPrice,
            @RequestParam @Parameter(description = "Preço máximo") BigDecimal maxPrice) {
        
        log.info("Buscando produtos por faixa de preço: {} - {}", minPrice, maxPrice);
        List<ProductResponseDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    /**
     * Busca textual de produtos
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar produtos por texto", 
               description = "Busca produtos por nome, descrição, categoria ou marca")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestParam @Parameter(description = "Termo de busca") String q) {
        
        log.info("Busca textual por: {}", q);
        List<ProductResponseDTO> products = productService.searchProducts(q);
        return ResponseEntity.ok(products);
    }

    /**
     * Busca com paginação
     */
    @GetMapping("/search-paginated")
    @Operation(summary = "Buscar produtos com paginação", 
               description = "Busca produtos com paginação e ordenação")
    public ResponseEntity<Page<ProductResponseDTO>> searchProductsWithPagination(
            @RequestParam @Parameter(description = "Termo de busca") String q,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.info("Busca paginada por: {} - Página: {}", q, pageable.getPageNumber());
        Page<ProductResponseDTO> products = productService.searchProductsWithPagination(q, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Listar categorias disponíveis
     */
    @GetMapping("/categories")
    @Operation(summary = "Listar categorias", 
               description = "Retorna todas as categorias de produtos disponíveis")
    public ResponseEntity<List<String>> getAvailableCategories() {
        log.info("Listando categorias disponíveis");
        List<String> categories = productService.getAvailableCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Listar marcas disponíveis
     */
    @GetMapping("/brands")
    @Operation(summary = "Listar marcas", 
               description = "Retorna todas as marcas de produtos disponíveis")
    public ResponseEntity<List<String>> getAvailableBrands() {
        log.info("Listando marcas disponíveis");
        List<String> brands = productService.getAvailableBrands();
        return ResponseEntity.ok(brands);
    }

    // ============= ENDPOINTS PROTEGIDOS (REQUER AUTENTICAÇÃO) =============

    /**
     * Criar novo produto (apenas admins)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar produto", 
               description = "Cria novo produto no catálogo (apenas admins)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "SKU já existe")
    })
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductDTO createProductDTO) {
        
        try {
            log.info("Request para criar produto: {}", createProductDTO.getName());
            ProductResponseDTO product = productService.createProduct(createProductDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Produto criado com sucesso");
            response.put("product", product);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Erro ao criar produto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Atualizar produto (apenas admins)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar produto", 
               description = "Atualiza dados do produto (apenas admins)")
    public ResponseEntity<?> updateProduct(
            @PathVariable @Parameter(description = "ID do produto") Long id,
            @Valid @RequestBody CreateProductDTO updateProductDTO) {
        
        try {
            log.info("Atualizando produto ID: {}", id);
            ProductResponseDTO product = productService.updateProduct(id, updateProductDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Produto atualizado com sucesso");
            response.put("product", product);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar produto ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Desativar produto (apenas admins)
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar produto", 
               description = "Desativa produto (soft delete) - apenas admins")
    public ResponseEntity<Map<String, String>> deactivateProduct(
            @PathVariable @Parameter(description = "ID do produto") Long id) {
        
        try {
            log.info("Desativando produto ID: {}", id);
            productService.deactivateProduct(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Produto desativado com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao desativar produto ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Reativar produto (apenas admins)
     */
    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reativar produto", 
               description = "Reativa produto desativado - apenas admins")
    public ResponseEntity<?> reactivateProduct(
            @PathVariable @Parameter(description = "ID do produto") Long id) {
        
        try {
            log.info("Reativando produto ID: {}", id);
            ProductResponseDTO product = productService.reactivateProduct(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Produto reativado com sucesso");
            response.put("product", product);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao reativar produto ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Deletar produto permanentemente (apenas admins)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar produto permanentemente", 
               description = "Remove produto permanentemente - apenas admins")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable @Parameter(description = "ID do produto") Long id) {
        
        try {
            log.info("Deletando produto permanentemente - ID: {}", id);
            productService.deleteProduct(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Produto deletado com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao deletar produto ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ============= ENDPOINTS DE ESTOQUE =============

    /**
     * Atualizar estoque (apenas admins)
     */
    @PatchMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar estoque", 
               description = "Atualiza quantidade em estoque - apenas admins")
    public ResponseEntity<?> updateStock(@Valid @RequestBody UpdateStockDTO updateStockDTO) {
        
        try {
            log.info("Atualizando estoque - Produto ID: {}", updateStockDTO.getProductId());
            ProductResponseDTO product = productService.updateStock(updateStockDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Estoque atualizado com sucesso");
            response.put("product", product);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar estoque: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Verificar disponibilidade de estoque
     */
    @GetMapping("/{id}/stock-check")
    @Operation(summary = "Verificar estoque", 
               description = "Verifica se há estoque disponível para quantidade solicitada")
    public ResponseEntity<Map<String, Object>> checkStockAvailability(
            @PathVariable @Parameter(description = "ID do produto") Long id,
            @RequestParam @Parameter(description = "Quantidade solicitada") Integer quantity) {
        
        log.info("Verificando estoque - Produto ID: {}, Quantidade: {}", id, quantity);
        boolean available = productService.checkStockAvailability(id, quantity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("product_id", id);
        response.put("requested_quantity", quantity);
        response.put("available", available);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Listar produtos com estoque baixo (apenas admins)
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Produtos com estoque baixo", 
               description = "Lista produtos com estoque baixo - apenas admins")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockProducts() {
        log.info("Listando produtos com estoque baixo");
        List<ProductResponseDTO> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Listar produtos sem estoque (apenas admins)
     */
    @GetMapping("/out-of-stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Produtos sem estoque", 
               description = "Lista produtos sem estoque - apenas admins")
    public ResponseEntity<List<ProductResponseDTO>> getOutOfStockProducts() {
        log.info("Listando produtos sem estoque");
        List<ProductResponseDTO> products = productService.getOutOfStockProducts();
        return ResponseEntity.ok(products);
    }

    // ============= ENDPOINTS DE RELATÓRIOS =============

    /**
     * Estatísticas do catálogo (apenas admins)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Estatísticas do catálogo", 
               description = "Retorna estatísticas gerais do catálogo - apenas admins")
    public ResponseEntity<Map<String, Object>> getCatalogStatistics() {
        log.info("Gerando estatísticas do catálogo");
        Map<String, Object> statistics = productService.getCatalogStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Listar todos os produtos (incluindo inativos - apenas admins)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os produtos", 
               description = "Lista todos os produtos incluindo inativos - apenas admins")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        log.info("Listando todos os produtos (incluindo inativos)");
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // ============= ENDPOINT DE SAÚDE =============

    /**
     * Health check do serviço
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", 
               description = "Verifica se o serviço está funcionando")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Product Service");
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
}
