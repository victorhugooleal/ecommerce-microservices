package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.CreateProductDTO;
import com.ecommerce.productservice.dto.ProductResponseDTO;
import com.ecommerce.productservice.dto.UpdateStockDTO;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service para gerenciamento de produtos
 * Implementa regras de negócio para CRUD de produtos e controle de estoque
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // ============= OPERAÇÕES CRUD =============

    /**
     * Criar novo produto
     */
    @Transactional
    public ProductResponseDTO createProduct(CreateProductDTO createProductDTO) {
        log.info("Criando produto: {}", createProductDTO.getName());

        // Verificar se SKU já existe
        if (createProductDTO.getSku() != null && productRepository.existsBySku(createProductDTO.getSku())) {
            throw new RuntimeException("SKU já está em uso: " + createProductDTO.getSku());
        }

        Product product = Product.builder()
                .name(createProductDTO.getName())
                .description(createProductDTO.getDescription())
                .price(createProductDTO.getPrice())
                .stockQuantity(createProductDTO.getStockQuantity())
                .category(createProductDTO.getCategory())
                .brand(createProductDTO.getBrand())
                .sku(createProductDTO.getSku())
                .weight(createProductDTO.getWeight())
                .dimensions(createProductDTO.getDimensions())
                .imageUrl(createProductDTO.getImageUrl())
                .active(createProductDTO.getActive())
                .featured(createProductDTO.getFeatured())
                .minStockLevel(createProductDTO.getMinStockLevel())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Produto criado com sucesso - ID: {}, SKU: {}", savedProduct.getId(), savedProduct.getSku());

        return convertToResponseDTO(savedProduct);
    }

    /**
     * Buscar produto por ID
     */
    public ProductResponseDTO getProductById(Long id) {
        log.info("Buscando produto por ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado - ID: " + id));
        return convertToResponseDTO(product);
    }

    /**
     * Buscar produto por SKU
     */
    public ProductResponseDTO getProductBySku(String sku) {
        log.info("Buscando produto por SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado - SKU: " + sku));
        return convertToResponseDTO(product);
    }

    /**
     * Listar todos os produtos
     */
    public List<ProductResponseDTO> getAllProducts() {
        log.info("Listando todos os produtos");
        return productRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar produtos ativos
     */
    public List<ProductResponseDTO> getActiveProducts() {
        log.info("Listando produtos ativos");
        return productRepository.findByActiveTrue().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar produtos disponíveis (com estoque)
     */
    public List<ProductResponseDTO> getAvailableProducts() {
        log.info("Listando produtos disponíveis");
        return productRepository.findAvailableProducts().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar produtos em destaque
     */
    public List<ProductResponseDTO> getFeaturedProducts() {
        log.info("Listando produtos em destaque");
        return productRepository.findByFeaturedTrueAndActiveTrue().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Atualizar produto
     */
    @Transactional
    public ProductResponseDTO updateProduct(Long id, CreateProductDTO updateProductDTO) {
        log.info("Atualizando produto ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado - ID: " + id));

        // Verificar se novo SKU já está em uso (se diferente do atual)
        if (updateProductDTO.getSku() != null && 
            !updateProductDTO.getSku().equals(product.getSku()) && 
            productRepository.existsBySku(updateProductDTO.getSku())) {
            throw new RuntimeException("SKU já está em uso: " + updateProductDTO.getSku());
        }

        // Atualizar dados
        product.setName(updateProductDTO.getName());
        product.setDescription(updateProductDTO.getDescription());
        product.setPrice(updateProductDTO.getPrice());
        product.setStockQuantity(updateProductDTO.getStockQuantity());
        product.setCategory(updateProductDTO.getCategory());
        product.setBrand(updateProductDTO.getBrand());
        product.setSku(updateProductDTO.getSku());
        product.setWeight(updateProductDTO.getWeight());
        product.setDimensions(updateProductDTO.getDimensions());
        product.setImageUrl(updateProductDTO.getImageUrl());
        product.setActive(updateProductDTO.getActive());
        product.setFeatured(updateProductDTO.getFeatured());
        product.setMinStockLevel(updateProductDTO.getMinStockLevel());
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        log.info("Produto atualizado com sucesso - ID: {}", updatedProduct.getId());

        return convertToResponseDTO(updatedProduct);
    }

    /**
     * Desativar produto (soft delete)
     */
    @Transactional
    public void deactivateProduct(Long id) {
        log.info("Desativando produto ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado - ID: " + id));

        product.setActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Produto desativado com sucesso - ID: {}", id);
    }

    /**
     * Reativar produto
     */
    @Transactional
    public ProductResponseDTO reactivateProduct(Long id) {
        log.info("Reativando produto ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado - ID: " + id));

        product.setActive(true);
        product.setUpdatedAt(LocalDateTime.now());
        Product reactivatedProduct = productRepository.save(product);

        log.info("Produto reativado com sucesso - ID: {}", id);
        return convertToResponseDTO(reactivatedProduct);
    }

    /**
     * Deletar produto permanentemente
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deletando produto permanentemente - ID: {}", id);

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado - ID: " + id);
        }

        productRepository.deleteById(id);
        log.info("Produto deletado permanentemente - ID: {}", id);
    }

    // ============= OPERAÇÕES DE ESTOQUE =============

    /**
     * Atualizar estoque
     */
    @Transactional
    public ProductResponseDTO updateStock(UpdateStockDTO updateStockDTO) {
        log.info("Atualizando estoque - Produto ID: {}, Operação: {}, Quantidade: {}", 
                updateStockDTO.getProductId(), updateStockDTO.getOperation(), updateStockDTO.getQuantity());

        Product product = productRepository.findById(updateStockDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado - ID: " + updateStockDTO.getProductId()));

        switch (updateStockDTO.getOperation()) {
            case REDUCE:
                product.reduceStock(updateStockDTO.getQuantity());
                break;
            case INCREASE:
                product.increaseStock(updateStockDTO.getQuantity());
                break;
            case SET:
                product.setStockQuantity(updateStockDTO.getQuantity());
                break;
        }

        product.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);

        log.info("Estoque atualizado - Produto ID: {}, Novo estoque: {}", 
                updatedProduct.getId(), updatedProduct.getStockQuantity());

        return convertToResponseDTO(updatedProduct);
    }

    /**
     * Verificar disponibilidade de estoque
     */
    public boolean checkStockAvailability(Long productId, Integer requestedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado - ID: " + productId));

        return product.getActive() && product.getStockQuantity() >= requestedQuantity;
    }

    /**
     * Produtos com estoque baixo
     */
    public List<ProductResponseDTO> getLowStockProducts() {
        log.info("Listando produtos com estoque baixo");
        return productRepository.findLowStockProducts().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Produtos sem estoque
     */
    public List<ProductResponseDTO> getOutOfStockProducts() {
        log.info("Listando produtos sem estoque");
        return productRepository.findOutOfStockProducts().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // ============= OPERAÇÕES DE BUSCA =============

    /**
     * Buscar produtos por categoria
     */
    public List<ProductResponseDTO> getProductsByCategory(String category) {
        log.info("Buscando produtos por categoria: {}", category);
        return productRepository.findByCategoryIgnoreCaseAndActiveTrue(category).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Buscar produtos por marca
     */
    public List<ProductResponseDTO> getProductsByBrand(String brand) {
        log.info("Buscando produtos por marca: {}", brand);
        return productRepository.findByBrandIgnoreCaseAndActiveTrue(brand).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Buscar produtos por faixa de preço
     */
    public List<ProductResponseDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Buscando produtos por faixa de preço: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceRangeAndActiveTrue(minPrice, maxPrice).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Busca textual
     */
    public List<ProductResponseDTO> searchProducts(String searchTerm) {
        log.info("Buscando produtos com termo: {}", searchTerm);
        return productRepository.searchProducts(searchTerm).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Busca com paginação
     */
    public Page<ProductResponseDTO> searchProductsWithPagination(String searchTerm, Pageable pageable) {
        log.info("Buscando produtos com termo '{}' - Página: {}", searchTerm, pageable.getPageNumber());
        return productRepository.searchProducts(searchTerm, pageable)
                .map(this::convertToResponseDTO);
    }

    // ============= OPERAÇÕES DE RELATÓRIO =============

    /**
     * Listar categorias disponíveis
     */
    public List<String> getAvailableCategories() {
        return productRepository.findDistinctActiveCategories();
    }

    /**
     * Listar marcas disponíveis
     */
    public List<String> getAvailableBrands() {
        return productRepository.findDistinctActiveBrands();
    }

    /**
     * Estatísticas do catálogo
     */
    public Map<String, Object> getCatalogStatistics() {
        log.info("Gerando estatísticas do catálogo");
        
        return Map.of(
                "total_products", productRepository.count(),
                "active_products", productRepository.countByActiveTrue(),
                "featured_products", productRepository.countByFeaturedTrueAndActiveTrue(),
                "total_stock_value", productRepository.getTotalStockValue() != null ? productRepository.getTotalStockValue() : BigDecimal.ZERO,
                "categories_count", productRepository.findDistinctActiveCategories().size(),
                "brands_count", productRepository.findDistinctActiveBrands().size(),
                "low_stock_count", productRepository.findLowStockProducts().size(),
                "out_of_stock_count", productRepository.findOutOfStockProducts().size()
        );
    }

    // ============= MÉTODOS AUXILIARES =============

    /**
     * Converter entidade para DTO de resposta
     */
    private ProductResponseDTO convertToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .brand(product.getBrand())
                .sku(product.getSku())
                .weight(product.getWeight())
                .dimensions(product.getDimensions())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .featured(product.getFeatured())
                .minStockLevel(product.getMinStockLevel())
                .available(product.isAvailable())
                .lowStock(product.isLowStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
