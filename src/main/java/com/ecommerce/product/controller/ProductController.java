package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CreateProductDTO;
import com.ecommerce.product.dto.ProductResponseDTO;
import com.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gerenciamento de produtos
 * Esta API será movida para o microsserviço de produtos
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "API para gerenciamento de produtos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Criar novo produto", description = "Cria um novo produto no catálogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody CreateProductDTO createProductDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(createProductDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos os produtos", description = "Retorna lista de todos os produtos ativos")
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produtos por categoria", description = "Retorna produtos de uma categoria específica")
    @ApiResponse(responseCode = "200", description = "Produtos da categoria retornados com sucesso")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(
            @Parameter(description = "Nome da categoria") @PathVariable String category) {
        List<ProductResponseDTO> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produtos por nome", description = "Busca produtos que contenham o nome especificado")
    @ApiResponse(responseCode = "200", description = "Resultados da busca retornados com sucesso")
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @Parameter(description = "Nome do produto a buscar") @RequestParam String name) {
        List<ProductResponseDTO> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produtos por faixa de preço", description = "Retorna produtos dentro de uma faixa de preço")
    @ApiResponse(responseCode = "200", description = "Produtos na faixa de preço retornados com sucesso")
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByPriceRange(
            @Parameter(description = "Preço mínimo") @RequestParam BigDecimal minPrice,
            @Parameter(description = "Preço máximo") @RequestParam BigDecimal maxPrice) {
        List<ProductResponseDTO> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza dados de um produto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Valid @RequestBody CreateProductDTO updateProductDTO) {
        try {
            ProductResponseDTO updatedProduct = productService.updateProduct(id, updateProductDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Desativar produto", description = "Desativa um produto (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateProduct(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        try {
            productService.deactivateProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Atualizar estoque", description = "Atualiza quantidade em estoque de um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateStock(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Parameter(description = "Nova quantidade em estoque") @RequestParam Integer quantity) {
        try {
            ProductResponseDTO updatedProduct = productService.updateStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias de produtos disponíveis")
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Produtos com estoque baixo", description = "Retorna produtos com estoque abaixo do limite especificado")
    @ApiResponse(responseCode = "200", description = "Produtos com estoque baixo retornados com sucesso")
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponseDTO>> getProductsWithLowStock(
            @Parameter(description = "Limite de estoque") @RequestParam(defaultValue = "10") Integer threshold) {
        List<ProductResponseDTO> products = productService.getProductsWithLowStock(threshold);
        return ResponseEntity.ok(products);
    }
}
